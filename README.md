# Sparkle
Spark Related Projects

## To build project

   sbt clean package
   
## To run applications

### Wordcount

    spark-submit --class com.naughtyzombie.sparkle.WordCount target/scala-2.10/sparkle_2.10-1.0.jar README.md target/wordcounts


TODO  
Lookup how to connect client to remote cluster  
setting up remote yarn client - might need to run Cloudera Manager  
