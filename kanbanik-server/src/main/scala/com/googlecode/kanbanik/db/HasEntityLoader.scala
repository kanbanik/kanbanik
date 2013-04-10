package com.googlecode.kanbanik.db

import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project

trait HasEntityLoader {
  def loadBoard(id: ObjectId, includeTasks: Boolean) = {
    try {
      Some(Board.byId(id, includeTasks))
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }
  
  def loadProject(id: ObjectId) = {
    try {
      Some(Project.byId(id))
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }
}