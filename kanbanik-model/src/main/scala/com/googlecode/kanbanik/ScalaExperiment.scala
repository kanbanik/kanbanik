package com.googlecode.kanbanik;

import com.mongodb.casbah.Imports._

class ScalaExperiment {
  
  def findAllNames(name: String) = {
    val mongoColl = MongoConnection()("kanbanik")("exp")
    for { x <- mongoColl} yield x(name)
  }
}