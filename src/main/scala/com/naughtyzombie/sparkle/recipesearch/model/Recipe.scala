package com.naughtyzombie.sparkle.recipesearch.model

/**
  * Created by pram on 08/11/2015.
  */
case class Recipe(id: String,
                  name: String,
                  source: String,
                  recipeYield: String,
                  ingredients: List[String],
                  prepTime: String,
                  cookTime: String,
                  datePublished: String,
                  description: String
                 )
