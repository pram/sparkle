package com.naughtyzombie.sparkle.aas.ch4

import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

/**
  * Created by pram on 07/12/2015.
  */
object DecisionTree {

  val logger = LoggerFactory.getLogger("DecisionTree")

  def main(args: Array[String]): Unit = {

    val sc = new SparkContext("local", "DecisionTree", new SparkConf().setAppName("DecisionTree"))
    val base = "files_x/covtype/"

  }
}
