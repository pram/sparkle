package com.naughtyzombie.sparkle.mlib

import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by pram on 19/11/2015.
  */
object MLibProcess {
  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf().setAppName("MLibExample")
    val sc = new SparkContext("local", "MLibExample", System.getenv("SPARK_HOME"))

    val spam = sc.textFile("files/spam.txt")
    val ham = sc.textFile("files/ham.txt")

    val tf = new HashingTF(numFeatures = 100)
    val spamFeatures = spam.map(email => tf.transform(email.split(" ")))
    val hamFeatures = ham.map(email => tf.transform(email.split(" ")))

    val positiveExamples = spamFeatures.map(features => LabeledPoint(1, features))
    val negativeExamples = hamFeatures.map(features => LabeledPoint(0, features))
    val trainingData = positiveExamples ++ negativeExamples
    trainingData.cache()

    val irLearner = new LogisticRegressionWithSGD()
    val model = irLearner.run(trainingData)

    val posTestExample = tf.transform("O M G GET cheap stuff by sending money to ...".split(" "))
    val negTestExample = tf.transform("Hi Dad, I started studying Spark the other ...".split(" "))

    println(s"Prediction for positive test example: ${model.predict(posTestExample)}")
    println(s"Prediction for negative test example: ${model.predict(negTestExample)}")

    sc.stop()
  }
}
