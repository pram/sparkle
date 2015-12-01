package com.naughtyzombie.sparkle.aas.ch3

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.slf4j.LoggerFactory

/**
  * Created by pram on 01/12/2015.
  */
object Recommender {

  val logger = LoggerFactory.getLogger("Recommender")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local","Recommender", new SparkConf().setAppName("Recommender"))

    val base = "files_x/audio/"

    val rawUserArtistData = sc.textFile(base + "user_artist_data.txt")
    val rawArtistData = sc.textFile(base + "artist_data.txt")
    val rawArtistAlias = sc.textFile(base + "artist_alias.txt")

    prepareData(rawUserArtistData, rawArtistData, rawArtistAlias)

    sc.stop
  }

  def prepareData(rawUserArtistData: RDD[String], rawArtistData: RDD[String], rawArtistAlias: RDD[String]) = {
    val userIDStats = rawUserArtistData.map(_.split(' ')(0).toDouble).stats()
    val itemIDStats = rawUserArtistData.map(_.split(' ')(1).toDouble).stats()
    logger.info(userIDStats.toString())
    logger.info(itemIDStats.toString())
  }

}
