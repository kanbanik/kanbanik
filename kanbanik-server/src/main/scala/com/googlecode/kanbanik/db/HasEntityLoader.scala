package com.googlecode.kanbanik.db

import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.model.Task

trait HasEntityLoader {
  def loadBoard(id: ObjectId, includeTasks: Boolean) = {
    if (id == null) {
      None
    } else {
      loadEntity[Board](id, Board.byId(_, includeTasks))
    }
  }

  def loadTask(id: ObjectId) = {
    loadEntity[Task](id, Task.byId(_))
  }
  
  def loadProject(id: ObjectId) = {
    loadEntity[Project](id, Project.byId(_))
  }

  def loadClassOfService(id: ObjectId) = {
    loadEntity[ClassOfService](id, ClassOfService.byId(_))
  }
  
  def loadUser(name: String) = {
    loadEntity[User, String](name, User.byId(_))
  }

  private def loadEntity[T](id: ObjectId, f: ObjectId => T): Option[T] = {
    loadEntity[T, ObjectId](id, f)
  }
  
  private def loadEntity[T, I](id: I, f: I => T): Option[T] = {
    try {
      Some(f(id))
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }
}