package com.googlecode.kanbanik.migrate

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.ManipulateUserDto
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.mongodb.DBObject
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.model.Project
import com.mongodb.BasicDBList
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.commands.SaveTaskCommand

class MigrateDb extends HasMongoConnection {
  
  val versionMigrations = Map(
    1 -> List(new From1To2, new From2To3),
    2 -> List(new From2To3))

  def migrateDbIfNeeded {
    using(createConnection) { conn =>
      val version = coll(conn, Coll.KanbanikVersion).findOne()
      if (version.isDefined) {
        val curVersion = version.get.get("version").asInstanceOf[Int]
        runAllFrom(curVersion)
      } else {
        coll(conn, Coll.KanbanikVersion) += MongoDBObject("version" -> 1)
        runAllFrom(1)
      }

    }
  }

  def runAllFrom(curVersion: Int) {
    val migrationParts = versionMigrations.get(curVersion)
    if (migrationParts.isDefined) {
      for (part <- migrationParts.get) part.migrate
    }
  }

}

trait MigrationPart extends HasMongoConnection {
  def migrate;

  def setVersionTo(version: Int) {
    using(createConnection) { conn =>
      coll(conn, Coll.KanbanikVersion).update(MongoDBObject(),
        $set("version" -> version))
    }
  }

}

class From1To2 extends MigrationPart {
  
  def migrate {

    // create a default user
    val userDto = new ManipulateUserDto(
      "admin",
      "Default User",
      1,
      "admin",
      "admin")

    // create the first user
    new CreateUserCommand().execute(new SimpleParams(userDto))

    // add isBalanceWorkflowitems support

    setVersionTo(2)
  }
}

class From2To3 extends MigrationPart {
  private val oldTasksCollection = "tasks"
  private lazy val taskBuilder = new TaskBuilder()
  
  def migrate {

    migrateTasks()

    setVersionTo(3)
  }

  def migrateTasks() {
    using(createConnection) { conn =>
      val oldTasks = coll(conn, oldTasksCollection).find().map(asOldEntity(_))
      val newTasks = oldTasks.map(_.asNewTask)
      for (val newTask <- newTasks if (newTask.project != null && newTask.workflowitem != null)) {
        newTask.store
      }
      
      cleanup
    }
    
    def cleanup() {
      using(createConnection) { conn =>
        coll(conn, oldTasksCollection).remove(MongoDBObject())
      }
    }
    
    def asOldEntity(dbObject: DBObject) = {
      new OldTask(
        Some(dbObject.get(Task.Fields.id.toString()).asInstanceOf[ObjectId]),
        dbObject.get(Task.Fields.name.toString()).asInstanceOf[String],
        dbObject.get(Task.Fields.description.toString()).asInstanceOf[String],
        dbObject.get(Task.Fields.classOfService.toString()).asInstanceOf[Int],
        dbObject.get(Task.Fields.ticketId.toString()).asInstanceOf[String],
        {
          val res = dbObject.get(Task.Fields.version.toString())
          if (res == null) {
            1
          } else {
            res.asInstanceOf[Int]
          }
        },
        dbObject.get(Task.Fields.workflowitem.toString()).asInstanceOf[ObjectId])

    }
  }

  class OldTask(val id: Option[ObjectId],
    val name: String,
    val description: String,
    val classOfService: Int,
    val ticketId: String,
    val version: Int,
    val workflowitemId: ObjectId) {

    def asNewTask(): Task = {
      new Task(
        None, // because I want to create a new one
        name,
        description,
        classOfService,
        ticketId,
        1, // because I basically want to create a new one
        findWorkflowitem(),
        findProject())

    }

    def findWorkflowitem(): Workflowitem = {
      val board = Board.all().find(board => board.workflow.containsItem(Workflowitem().withId(workflowitemId))).getOrElse(return null)
      val workflowitem = board.workflow.findItem(Workflowitem().withId(workflowitemId))
      workflowitem.getOrElse(null)
    }

    def findProject(): Project = {
      using(createConnection) { conn =>
        val projects = for (val project <- coll(conn, Coll.Projects).find() if (isOnProject(project))) yield project
        if (projects == null || projects.isEmpty) {
          null
        } else {
          val projectId = projects.next.get(Project.Fields.id.toString())
          Project().withId(projectId.asInstanceOf[ObjectId])
        }
      }
    }

    def isOnProject(dbObject: DBObject): Boolean = {
      val tasks = dbObject.get("tasks")
      if (tasks == null || tasks == None) {
        false
      } else {
        if (tasks.isInstanceOf[List[ObjectId]]) {
          tasks.asInstanceOf[List[ObjectId]].contains(id.get)
        } else {
          tasks.asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[ObjectId]].contains(id.get)
        }
      }
    }
  }
}