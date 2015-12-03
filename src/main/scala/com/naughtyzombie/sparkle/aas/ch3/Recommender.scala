package com.naughtyzombie.sparkle.aas.ch3

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.recommendation._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.slf4j.LoggerFactory

import scala.collection.Map
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Created by pram on 01/12/2015.
  */
object Recommender {

  val logger = LoggerFactory.getLogger("Recommender")

  val USER_ID  = 1005491

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local", "Recommender", new SparkConf().setAppName("Recommender"))

    val base = "files_x/audio/"

    val rawUserArtistData = sc.textFile(base + "user_artist_data_small.txt")
    val rawArtistData = sc.textFile(base + "artist_data.txt")
    val rawArtistAlias = sc.textFile(base + "artist_alias.txt")

    prepareData(rawUserArtistData, rawArtistData, rawArtistAlias)
    modelData(sc,rawUserArtistData, rawArtistData, rawArtistAlias)
    evaluate(sc, rawUserArtistData, rawArtistAlias)
    recommend(sc,rawUserArtistData,rawArtistData, rawArtistAlias)

    sc.stop
  }

  def recommend(sc: SparkContext,rawUserArtistData: RDD[String],rawArtistData: RDD[String],rawArtistAlias: RDD[String]): Unit = {

    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))
    val allData = buildRatings(rawUserArtistData, bArtistAlias).cache()
    val model = ALS.trainImplicit(allData, 50, 10, 1.0, 40.0)
    allData.unpersist()

    val userID = USER_ID
    val recommendations = model.recommendProducts(userID, 5)
    val recommendedProductIDs = recommendations.map(_.product).toSet

    val artistByID = buildArtistByID(rawArtistData)

    artistByID.filter { case (id, name) => recommendedProductIDs.contains(id) }.
      values.collect().foreach{ i =>
      logger.info(s"recIds $i")
    }

    val someUsers = allData.map(_.user).distinct().take(100)
    val someRecommendations = someUsers.map(userID => model.recommendProducts(userID, 5))
    someRecommendations.map(
      recs => recs.head.user + " -> " + recs.map(_.product).mkString(", ")
    ).foreach{ i =>
      logger.info(s"recs $i")
    }

    unpersist(model)
  }

  def evaluate(sc: SparkContext,rawUserArtistData: RDD[String],rawArtistAlias: RDD[String]): Unit = {
    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))
    val allData = buildRatings(rawUserArtistData, bArtistAlias)

    val Array(trainData, cvData) = allData.randomSplit(Array(0.9, 0.1))
    trainData.cache()
    cvData.cache()

    val allItemIDs = allData.map(_.product).distinct().collect()
    val bAllItemIDs = sc.broadcast(allItemIDs)

    val mostListenedAUC = areaUnderCurve(cvData, bAllItemIDs, predictMostListened(sc, trainData))
    logger.info(s"mostListenedAUC $mostListenedAUC")

    val evaluations =
      for (rank <- Array(10, 50);
           lambda <- Array(1.0, 0.0001);
           alpha <- Array(1.0, 40.0))
        yield {
          val model = ALS.trainImplicit(trainData, rank, 10, lambda, alpha)
          val auc = areaUnderCurve(cvData, bAllItemIDs, model.predict)
          unpersist(model)
          ((rank, lambda, alpha), auc)
        }

    evaluations.sortBy(_._2).reverse.foreach{ i =>
      logger.info(s"evaluations $i")
    }

    trainData.unpersist()
    cvData.unpersist()
    logger.info("End Evaluation")
  }

  def predictMostListened(sc: SparkContext, train: RDD[Rating])(allData: RDD[(Int,Int)]) = {
    val bListenCount = sc.broadcast(train.map(r => (r.product, r.rating)).reduceByKey(_ + _).collectAsMap())

    allData.map { case (user, product) =>
      Rating(user, product, bListenCount.value.getOrElse(product, 0.0))
    }
  }

  def areaUnderCurve(positiveData: RDD[Rating],bAllItemIDs: Broadcast[Array[Int]],predictFunction: (RDD[(Int,Int)] => RDD[Rating])) = {

    val positiveUserProducts = positiveData.map(r => (r.user, r.product))

    val positivePredictions = predictFunction(positiveUserProducts).groupBy(_.user)

    val negativeUserProducts = positiveUserProducts.groupByKey().mapPartitions {
      // mapPartitions operates on many (user,positive-items) pairs at once
      userIDAndPosItemIDs => {
        // Init an RNG and the item IDs set once for partition
        val random = new Random()
        val allItemIDs = bAllItemIDs.value
        userIDAndPosItemIDs.map { case (userID, posItemIDs) =>
          val posItemIDSet = posItemIDs.toSet
          val negative = new ArrayBuffer[Int]()
          var i = 0
          // Keep about as many negative examples per user as positive.
          // Duplicates are OK
          while (i < allItemIDs.size && negative.size < posItemIDSet.size) {
            val itemID = allItemIDs(random.nextInt(allItemIDs.size))
            if (!posItemIDSet.contains(itemID)) {
              negative += itemID
            }
            i += 1
          }
          // Result is a collection of (user,negative-item) tuples
          negative.map(itemID => (userID, itemID))
        }
      }
    }.flatMap(t => t)
    // flatMap breaks the collections above down into one big set of tuples

    // Make predictions on the rest:
    val negativePredictions = predictFunction(negativeUserProducts).groupBy(_.user)

    // Join positive and negative by user
    positivePredictions.join(negativePredictions).values.map {
      case (positiveRatings, negativeRatings) =>
        // AUC may be viewed as the probability that a random positive item scores
        // higher than a random negative one. Here the proportion of all positive-negative
        // pairs that are correctly ranked is computed. The result is equal to the AUC metric.
        var correct = 0L
        var total = 0L
        // For each pairing,
        for (positive <- positiveRatings;
             negative <- negativeRatings) {
          // Count the correctly-ranked pairs
          if (positive.rating > negative.rating) {
            correct += 1
          }
          total += 1
        }
        // Return AUC: fraction of pairs ranked correctly
        correct.toDouble / total
    }.mean() // Return mean AUC over users
  }

  def modelData(sc: SparkContext, rawUserArtistData: RDD[String], rawArtistData: RDD[String], rawArtistAlias: RDD[String]) = {
    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))

    val trainData = buildRatings(rawUserArtistData, bArtistAlias).cache()

    val model = ALS.trainImplicit(trainData, 10, 5, 0.01, 1.0)

    trainData.unpersist()

    logger.info(model.userFeatures.mapValues(_.mkString(", ")).first().toString())

    val userID = USER_ID
    val recommendations = model.recommendProducts(userID, 5)
    recommendations.foreach{ i =>
      logger.info(s"Recommendations $i")
    }

    val recommendedProductIDs = recommendations.map(_.product).toSet

    val rawArtistsForUser = rawUserArtistData.map(_.split(' ')).filter {
      case Array(user,_,_) => user.toInt == userID
    }

    val existingProducts = rawArtistsForUser.map {
      case Array(_,artist,_) => artist.toInt
    }.collect().toSet

    val artistByID = buildArtistByID(rawArtistData)

    artistByID.filter { case (id, name) => existingProducts.contains(id) }.values.collect().foreach{ i =>
      logger.info(s"existing $i")
    }
    artistByID.filter { case (id, name) => recommendedProductIDs.contains(id) }.values.collect().foreach{ i =>
      logger.info(s"Recommended Product Ids $i")
    }

    unpersist(model)

  }

  def unpersist(model: MatrixFactorizationModel): Unit = {
    model.userFeatures.unpersist()
    model.productFeatures.unpersist()
  }

  def buildRatings(rawUserArtistData: RDD[String], bArtistAlias: Broadcast[Map[Int,Int]]) = {
    rawUserArtistData.map { line =>
      val Array(userID, artistID, count) = line.split(' ').map(_.toInt)
      val finalArtistID = bArtistAlias.value.getOrElse(artistID, artistID)
      Rating(userID, finalArtistID, count)
    }
  }

  def prepareData(rawUserArtistData: RDD[String], rawArtistData: RDD[String], rawArtistAlias: RDD[String]) = {
    val userIDStats = rawUserArtistData.map(_.split(' ')(0).toDouble).stats()
    val itemIDStats = rawUserArtistData.map(_.split(' ')(1).toDouble).stats()
    logger.info(userIDStats.toString())
    logger.info(itemIDStats.toString())

    val artistByID = buildArtistByID(rawArtistData)
    val artistAlias = buildArtistAlias(rawArtistAlias)

    val (badID, goodID) = artistAlias.head
    logger.info(artistByID.lookup(badID) + " -> " + artistByID.lookup(goodID))
  }

  def buildArtistByID(rawArtistData: RDD[String]) =
    rawArtistData.flatMap { line =>
      val (id, name) = line.span(_ != '\t')
      if (name.isEmpty) {
        None
      } else {
        try {
          Some((id.toInt, name.trim))
        } catch {
          case e: NumberFormatException => None
        }
      }
    }

  def buildArtistAlias(rawArtistAlias: RDD[String]): Map[Int, Int] = {
    rawArtistAlias.flatMap { line =>
      val tokens = line.split('\t')
      if (tokens(0).isEmpty) {
        None
      } else {
        Some((tokens(0).toInt, tokens(1).toInt))
      }
    }.collectAsMap()
  }


}
