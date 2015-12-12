package com.naughtyzombie.sparkle.aas.ch5

import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.tree.{DecisionTree, RandomForest}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory

/**
  * Created by pram on 07/12/2015.
  */
object AnomalyDetection {

  val logger = LoggerFactory.getLogger("AnomalyDetection")

  def main(args: Array[String]): Unit = {

    val sc = new SparkContext("local", "AnomalyDetection", new SparkConf().setAppName("AnomalyDetection"))
    val base = "files_x/kdd/"

    val rawData = sc.textFile(base + "covtype.data")

  }
}
