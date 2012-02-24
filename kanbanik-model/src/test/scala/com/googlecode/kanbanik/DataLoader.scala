package com.googlecode.kanbanik
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject

object DataLoader {

  val mongoColl = MongoConnection()("kanbanik")("exp")

  def fillDB() {
    mongoColl += MongoDBObject("name" -> "name1")
    mongoColl += MongoDBObject("name" -> "name2")
    mongoColl += MongoDBObject("name" -> "name3")
    mongoColl += MongoDBObject("name" -> "name4")
  }

  def clearDB() {
    mongoColl.find().foreach { 
      mongoColl.remove(_)
    }
  }
}