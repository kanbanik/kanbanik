package com.googlecode.kanbanik.db

import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.ClassOfService

trait HasEntityLoader {
  def loadBoard(id: ObjectId, includeTasks: Boolean) = {
    if (id == null) {
      None
    } else {
      loadEntity[Board](id, Board.byId(_, includeTasks))
    }
  }

  def loadProject(id: ObjectId) = {
    loadEntity[Project](id, Project.byId(_))
  }

  def loadClassOfService(id: ObjectId) = {
    loadEntity[ClassOfService](id, ClassOfService.byId(_))
  }

  private def loadEntity[T](id: ObjectId, f: ObjectId => T): Option[T] = {
    try {
      Some(f(id))
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }
}