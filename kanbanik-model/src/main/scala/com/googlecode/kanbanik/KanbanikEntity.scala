package com.googlecode.kanbanik
import com.mongodb.casbah.MongoConnection

class KanbanikEntity {
  
  val conn = MongoConnection()("kanbanik")

  object Coll extends Enumeration {
    val Workflowitems = Value("workflowitems")
    val Boards = Value("boards")
    
  }

  def coll(collName: Coll.Value) = {
    conn(collName.toString())
  }

}