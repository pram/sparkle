package com.naughtyzombie.sparkle.recipesearch

import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.Vector

/**
  * Created by pram on 11/11/2015.
  */
object Utils {

  val numFeatures = 1000
  val tf = new HashingTF(numFeatures)

  def featurize(s: String): Vector = {
    tf.transform(s.sliding(2).toSeq)
  }

}
