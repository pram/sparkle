package com.naughtyzombie.sparkle.recipesearch

import java.io.File

import com.google.gson.{GsonBuilder, JsonParser}
import org.apache.spark._
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.rdd.RDD
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

    val jsonParser = new JsonParser()
    val gson = new GsonBuilder().setPrettyPrinting().create()

    val modelRecipeNames = sqlContext.sql("select name from recipesSource where name like '%chicken%'").map(_.toString)

    val vectors = modelRecipeNames.map(Utils.featurize).cache()

    vectors.count()

    val maxIterations: Int = 50
    val model = KMeans.train(vectors,5,maxIterations)

    val outFile: String = "output/out.txt"
    new File(outFile).delete()
    sc.makeRDD(model.clusterCenters, maxIterations).saveAsObjectFile(outFile)

    val allRecipeNames = sqlContext.sql("select name from recipesSource").map(_.toString)
    val filteredRecipes: RDD[String] = allRecipeNames.filter { t => model.predict(Utils.featurize(t)) == 9}
    for (recipe <- filteredRecipes.take(10)) {
      println(recipe)
    }

  }
}
