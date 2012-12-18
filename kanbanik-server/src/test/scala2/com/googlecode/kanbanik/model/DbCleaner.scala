package com.googlecode.kanbanik.model

import com.googlecode.kanbanik.db.HasMongoConnection

object DbCleaner extends HasMongoConnection  {
  
  val workflowitems = createConnection("kanbanik")("workflowitems")
  val boards = createConnection("kanbanik")("boards")
  val tasks = createConnection("kanbanik")("tasks")
  val projects = createConnection("kanbanik")("projects")
  
	def clearDb() {
	  workflowitems.find().foreach {
      workflowitems.remove(_)
    }

    boards.find().foreach {
      boards.remove(_)
    }

    tasks.find().foreach {
      tasks.remove(_)
    }
    
    projects.find().foreach {
      projects.remove(_)
    }
	}
}