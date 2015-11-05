import AssemblyKeys._

import sys.process._
import java.net.URL
import java.io.File


name := "sparkle"

version := "1.0"

scalaVersion := "2.10.4"

val sparkVersion: String = "1.4.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion % "provided"

libraryDependencies += "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"

libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"

libraryDependencies += "org.apache.spark" %% "spark-streaming-twitter" % sparkVersion

libraryDependencies += "com.google.code.gson" % "gson" % "2.3"

libraryDependencies += "org.twitter4j" % "twitter4j-core" % "3.0.3"

libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

assemblySettings

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}

lazy val downloadRecipeFile = taskKey[Unit]("Download the recipe archive and extract to /input")

downloadRecipeFile := {
  if(java.nio.file.Files.notExists(new File("input","recipeitems-latest.json").toPath())) {
    println("Downloading File")
    new File("input").mkdir()
    new File("input").listFiles().foreach{ file => file.delete()}
    IO.download(new URL("http://openrecipes.s3.amazonaws.com/recipeitems-latest.json.gz"), new File("input","recipeitems-latest.json.gz"))
    println("Extracting File")
    IO.gunzip(new File("input","recipeitems-latest.json.gz"), new File("input","recipeitems-latest.json"))
  } else {
    println("File already downloaded")
  }
}