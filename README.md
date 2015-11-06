# Sparkle
Spark Related Projects

## To build project

   sbt clean package
   
## To run applications

### Wordcount

    spark-submit --class com.naughtyzombie.sparkle.WordCount target/scala-2.10/sparkle_2.10-1.0.jar README.md target/wordcounts
    
running on the server. Upload files to server
    
    spark-submit --class com.naughtyzombie.sparkle.WordCount --master spark://192.168.0.43:7077 --deploy-mode cluster target/scala-2.10/sparkle_2.10-1.0.jar /user/guest/README.md /user/guest/READMEOUT
    
### Recipesearch

To download the source file

    sbt downloadRecipeFile
    
To run recipe search

    spark-submit --class com.naughtyzombie.sparkle.recipesearch.RecipeMain target/scala-2.10/sparkle_2.10-1.0.jar local input/recipeitems-latest.json output/chicken.txt


TODO  
Download recipe file. Write a separate spark job to ingest file  
Stream recipe file. Adapt spark job to ingest file from streaming source.  
Add machine learning to look for certain types of files  
?SparkSQL usage?  
?Create Graph of Recipes?  
Lookup how to connect client to remote cluster    
setting up remote yarn client - might need to run Cloudera Manager    
