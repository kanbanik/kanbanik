package com.googlecode.kanbanik;

import com.mongodb.casbah.Imports._

class ScalaExperiment extends KanbanikEntity {
  
  object Collections extends Enumeration {
  type Collections = Value
  val Exp = Value("exp")
}

  
  def findAllNames(name: String) = {
    val mongoColl = coll(Coll.Exp)
    for { x <- mongoColl} yield x(name)
  }
}