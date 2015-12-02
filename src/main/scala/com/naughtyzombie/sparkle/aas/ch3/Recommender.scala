package com.naughtyzombie.sparkle.aas.ch3

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.mllib.recommendation._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.slf4j.LoggerFactory

import scala.collection.Map

/**
  * Created by pram on 01/12/2015.
  */
object Recommender {

  val logger = LoggerFactory.getLogger("Recommender")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local", "Recommender", new SparkConf().setAppName("Recommender"))

    val base = "files_x/audio/"

    val rawUserArtistData = sc.textFile(base + "user_artist_data_small.txt")
    val rawArtistData = sc.textFile(base + "artist_data.txt")
    val rawArtistAlias = sc.textFile(base + "artist_alias.txt")

    prepareData(rawUserArtistData, rawArtistData, rawArtistAlias)
    modelData(sc,rawUserArtistData, rawArtistData, rawArtistAlias)

    sc.stop
  }

  def modelData(sc: SparkContext, rawUserArtistData: RDD[String], rawArtistData: RDD[String], rawArtistAlias: RDD[String]) = {
    val bArtistAlias = sc.broadcast(buildArtistAlias(rawArtistAlias))

    val trainData = buildRatings(rawUserArtistData, bArtistAlias).cache()

    val model = ALS.trainImplicit(trainData, 10, 5, 0.01, 1.0)

    trainData.unpersist()

    logger.info(model.userFeatures.mapValues(_.mkString(", ")).first().toString())

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
