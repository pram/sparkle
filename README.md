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

For reference, some line samples

    { "_id" : { "$oid" : "5160756b96cc62079cc2db15" }, "name" : "Drop Biscuits and Sausage Gravy", "ingredients" : "Biscuits\n3 cups All-purpose Flour\n2 Tablespoons Baking Powder\n1/2 teaspoon Salt\n1-1/2 stick (3/4 Cup) Cold Butter, Cut Into Pieces\n1-1/4 cup Butermilk\n SAUSAGE GRAVY\n1 pound Breakfast Sausage, Hot Or Mild\n1/3 cup All-purpose Flour\n4 cups Whole Milk\n1/2 teaspoon Seasoned Salt\n2 teaspoons Black Pepper, More To Taste", "url" : "http://thepioneerwoman.com/cooking/2013/03/drop-biscuits-and-sausage-gravy/", "image" : "http://static.thepioneerwoman.com/cooking/files/2013/03/bisgrav.jpg", "ts" : { "$date" : 1365276011104 }, "cookTime" : "PT30M", "source" : "thepioneerwoman", "recipeYield" : "12", "datePublished" : "2013-03-11", "prepTime" : "PT10M", "description" : "Late Saturday afternoon, after Marlboro Man had returned home with the soccer-playing girls, and I had returned home with the..." }
    { "_id" : { "$oid" : "5160756d96cc62079cc2db16" }, "name" : "Hot Roast Beef Sandwiches", "ingredients" : "12 whole Dinner Rolls Or Small Sandwich Buns (I Used Whole Wheat)\n1 pound Thinly Shaved Roast Beef Or Ham (or Both!)\n1 pound Cheese (Provolone, Swiss, Mozzarella, Even Cheez Whiz!)\n1/4 cup Mayonnaise\n3 Tablespoons Grated Onion (or 1 Tbsp Dried Onion Flakes))\n1 Tablespoon Poppy Seeds\n1 Tablespoon Spicy Mustard\n1 Tablespoon Horseradish Mayo Or Straight Prepared Horseradish\n Dash Of Worcestershire\n Optional Dressing Ingredients: Sriracha, Hot Sauce, Dried Onion Flakes Instead Of Fresh, Garlic Powder, Pepper, Etc.)", "url" : "http://thepioneerwoman.com/cooking/2013/03/hot-roast-beef-sandwiches/", "image" : "http://static.thepioneerwoman.com/cooking/files/2013/03/sandwiches.jpg", "ts" : { "$date" : 1365276013902 }, "cookTime" : "PT20M", "source" : "thepioneerwoman", "recipeYield" : "12", "datePublished" : "2013-03-13", "prepTime" : "PT20M", "description" : "When I was growing up, I participated in my Episcopal church's youth group, and I have lots of memories of weekly meetings wh..." }
    { "_id" : { "$oid" : "5160756f96cc6207a37ff777" }, "name" : "Morrocan Carrot and Chickpea Salad", "ingredients" : "Dressing:\n1 tablespoon cumin seeds\n1/3 cup / 80 ml extra virgin olive oil\n2 tablespoons fresh lemon juice\n1 tablespoon honey\n1/2 teaspoon fine sea salt, plus more to taste\n1/8 teaspoon cayenne pepper\n10 ounces carrots, shredded on a box grater or sliced whisper thin on a mandolin\n2 cups cooked chickpeas (or one 15- ounce can, drained and rinsed)\n2/3 cup / 100 g  dried pluots, plums, or dates cut into chickpea-sized pieces\n1/3 cup / 30 g fresh mint, torn\nFor serving: lots of toasted almond slices, dried or fresh rose petals - all optional (but great additions!)", "url" : "http://www.101cookbooks.com/archives/moroccan-carrot-and-chickpea-salad-recipe.html", "image" : "http://www.101cookbooks.com/mt-static/images/food/moroccan_carrot_salad_recipe.jpg", "ts" : { "$date" : 1365276015332 }, "datePublished" : "2013-01-07", "source" : "101cookbooks", "prepTime" : "PT15M", "description" : "A beauty of a carrot salad - tricked out with chickpeas, chunks of dried pluots, sliced almonds, and a toasted cumin dressing. Thank you Diane Morgan." }
    { "_id" : { "$oid" : "5160757096cc62079cc2db17" }, "name" : "Mixed Berry Shortcake", "ingredients" : "Biscuits\n3 cups All-purpose Flour\n2 Tablespoons Baking Powder\n3 Tablespoons Sugar\n1/2 teaspoon Salt\n1-1/2 stick (3/4 Cup) Cold Butter, Cut Into Pieces\n1-1/4 cup Buttermilk\n1/2 teaspoon Almond Extract (optional)\n Berries\n2 pints Mixed Berries And/or Sliced Strawberries\n1/3 cup Sugar\n Zest And Juice Of 1 Small Orange\n SWEET YOGURT CREAM\n1 package (7 Ounces) Plain Greek Yogurt\n1 cup Cold Heavy Cream\n1/2 cup Sugar\n2 Tablespoons Brown Sugar", "url" : "http://thepioneerwoman.com/cooking/2013/03/mixed-berry-shortcake/", "image" : "http://static.thepioneerwoman.com/cooking/files/2013/03/shortcake.jpg", "ts" : { "$date" : 1365276016700 }, "cookTime" : "PT15M", "source" : "thepioneerwoman", "recipeYield" : "8", "datePublished" : "2013-03-18", "prepTime" : "PT15M", "description" : "It's Monday! It's a brand new week! The birds are chirping! The coffee's brewing! Everything has such hope and promise!     A..." }
    
To run recipe search

    spark-submit --class com.naughtyzombie.sparkle.recipesearch.RecipeMain target/scala-2.10/sparkle_2.10-1.0.jar local input/recipeitems-latest.json output/chicken.txt


TODO  

Download recipe file. Write a separate spark job to ingest file  
Stream recipe file. Adapt spark job to ingest file from streaming source.  
Add machine learning to look for certain types of files  
Spark JSON Parsing  
?SparkSQL usage?  
?Create Graph of Recipes?  
Lookup how to connect client to remote cluster    
setting up remote yarn client - might need to run Cloudera Manager    
