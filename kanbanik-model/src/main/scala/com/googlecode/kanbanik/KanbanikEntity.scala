package com.googlecode.kanbanik
import com.mongodb.casbah.MongoConnection

class KanbanikEntity {
  
  val conn = MongoConnection()("kanbanik")

  object Coll extends Enumeration {
    val Exp = Value("exp")
    
    val Workflowitems = Value("workflowitems")
    
  }

  def coll(collName: Coll.Value) = {
    conn(collName.toString())
  }

}