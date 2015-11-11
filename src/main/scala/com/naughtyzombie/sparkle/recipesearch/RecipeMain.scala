package com.naughtyzombie.sparkle.recipesearch

import com.naughtyzombie.sparkle.recipesearch.model.SourceRecipe
import org.apache.spark._
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import org.apache.spark.sql.SQLContext
import org.slf4j.LoggerFactory

/**
  * Created by pram on 05/11/2015.
  */
object RecipeMain {
  def logger = LoggerFactory.getLogger("RecipeMain")
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      logger.error(s"Usage: RecipeMain sparkmaster inputfile outputfile")
      sys.exit(1)
    }

    val master = args(0)
    val inputFile = args(1)
    val outputFile = args(2)

    val sc = new SparkContext(master, "RecipeMain", System.getenv("SPARK_HOME"))

    val sqlContext = new SQLContext(sc)

    val recipesSource = sqlContext.read.json(inputFile)

    recipesSource.registerTempTable("recipesSource")

    val x = sqlContext.sql("select count(*) from recipesSource")
    val y = sqlContext.sql("select count(*) from recipesSource where description like '%rice%'")

    println(x.collectAsList())
    println(y.collectAsList())
  }
}
