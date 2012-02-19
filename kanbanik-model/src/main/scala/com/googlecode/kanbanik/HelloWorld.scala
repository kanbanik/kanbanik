package com.googlecode.kanbanik;

import com.mongodb.casbah.Imports._

class HelloWorld {
  def enrichName(name: String) = {

    val mongoColl = MongoConnection()("kanbanik")("exp")
    
    var names = ""
      
    mongoColl.find().foreach { x =>
       names += x("prevName")
    }

    mongoColl += MongoDBObject("prevName" -> name)

    name + names + " From Scala"
  }
}