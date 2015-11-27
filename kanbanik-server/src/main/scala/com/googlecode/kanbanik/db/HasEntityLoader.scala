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

  def loadTask(id: ObjectId, user: User) = {
    loadEntity[Task](id, Task.byId(_, user))
  }
  
  def loadProject(id: ObjectId, user: User) = {
    loadEntity[Project](id, Project.byId(_, user))
  }

  def loadClassOfService(id: ObjectId) = {
    loadEntity[ClassOfService](id, ClassOfService.byId)
  }
  
  def loadUser(name: String) = {
    loadEntity[User, String](name, User.byId)
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