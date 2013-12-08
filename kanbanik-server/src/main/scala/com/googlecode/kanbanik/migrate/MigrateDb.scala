package com.googlecode.kanbanik.migrate

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.mongodb.DBObject
import com.googlecode.kanbanik.model._
import org.bson.types.ObjectId
import com.mongodb.BasicDBList
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.commands.MoveTaskCommand
import com.googlecode.kanbanik.commons._
import scala.Some
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing
import com.googlecode.kanbanik.dtos.ManipulateUserDto

class MigrateDb extends HasMongoConnection {

  val versionMigrations = Map(
    1 -> List(new From1To2, new From2To3),
    2 -> List(new From2To3))

  def migrateDbIfNeeded {
    using(createConnection) {
      conn =>
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
    using(createConnection) {
      conn =>
        coll(conn, Coll.KanbanikVersion).update(MongoDBObject(),
          $set("version" -> version))
    }
  }

}

class From1To2 extends MigrationPart {

  def migrate {

    // create a default user
    val userDto = ManipulateUserDto(
      "admin",
      "Default User",
      null,
      "sessionId",
      1,
      "admin",
      "admin")

    // create the first user
    new CreateUserCommand().execute(userDto)

    setVersionTo(2)
  }
}

class From2To3 extends MigrationPart {
  private val oldTasksCollection = "tasks"
  private val boardsCollection = "boards"

  private lazy val taskBuilder = new TaskBuilder()

  def migrate {

    val classesOfServices = createDefaultClassesOfService()

    migrateBoard

    migrateTasks(classesOfServices)

    setVersionTo(3)

    // do not do any cleanup - if something goes wrong than at least the old data will stay in place
  }

  def migrateBoard() {

    def migrateBalanced(board: DBObject, boards: List[Board]) {
      val id = board.get(Board.Fields.id.toString()).asInstanceOf[ObjectId]
      val balanced = board.get("balanceWorkflowitems")

      val isBalanced = if (balanced == null) true else balanced.asInstanceOf[Boolean]

      if (!isBalanced) {
        // consider only non balanced one as the balanced is the default
        val boardToUpdate = boards.find(_.id.get == id).getOrElse(return)
        boardToUpdate.copy(workfloVerticalSizing = WorkfloVerticalSizing.MIN_POSSIBLE).store
      }
    }

    using(createConnection) {
      conn =>
        val rawBoards = coll(conn, Coll.Boards).find(MongoDBObject())
        val realBoards = Board.all(false)
        rawBoards.foreach(migrateBalanced(_, realBoards))
    }

  }

  def createDefaultClassesOfService() = {
    Map(2 -> new ClassOfService(
      None,
      "Standard",
      "For typical tasks that flow through the system.",
      "5d8eef",
      1).store) + (0 -> new ClassOfService(
      None,
      "Expedite",
      "For critical tasks. Can break all rules on the system.",
      "e21714",
      1).store) + (3 -> new ClassOfService(
      None,
      "Intangible",
      "Nice to have but not critical.",
      "1eaa25",
      1).store) + (1 -> new ClassOfService(
      None,
      "Fixed Delivery Date",
      "Has to be done until specified date.",
      "ffeb00",
      1).store)
  }

  def migrateTasks(classesOfService: Map[Int, ClassOfService]) {
    using(createConnection) {
      conn =>
        val oldTasks = coll(conn, oldTasksCollection).find().map(asOldEntity(_))
        val newTasks = oldTasks.map(_.asNewTask(classesOfService))
        var order = 0
        for (newTask <- newTasks if (newTask.project != null && newTask.workflowitem != null)) {
          newTask.copy(order = Integer.toString(order)).store
          order += 100
        }

    }

    def asOldEntity(dbObject: DBObject) = {
      new OldTask(
        Some(dbObject.get(Task.Fields.id.toString()).asInstanceOf[ObjectId]),
        dbObject.get(Task.Fields.name.toString()).asInstanceOf[String],
        dbObject.get(Task.Fields.description.toString()).asInstanceOf[String],
        dbObject.get(Task.Fields.classOfService.toString()).asInstanceOf[Int],
        dbObject.get(Task.Fields.ticketId.toString()).asInstanceOf[String], {
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

    def asNewTask(classesOfService: Map[Int, ClassOfService]): Task = {
      new Task(
        None, // because I want to create a new one
        name,
        description,
        {
          if (classesOfService.contains(classOfService)) {
            classesOfService.get(classOfService)
          } else {
            classesOfService.get(2)
          }

        },
        ticketId,
        1, // because I basically want to create a new one
        "",
        None,
        "",
        findWorkflowitem(),
        findProject())

    }

    def findWorkflowitem(): Workflowitem = {
      val board = Board.all(false).find(board => board.workflow.containsItem(Workflowitem().copy(id = Some(workflowitemId)))).getOrElse(return null)
      val workflowitem = board.workflow.findItem(Workflowitem().copy(id = Some(workflowitemId)))
      workflowitem.getOrElse(null)
    }

    def findProject(): Project = {
      using(createConnection) {
        conn =>
          val projects = for (project <- coll(conn, Coll.Projects).find() if (isOnProject(project))) yield project
          if (projects == null || projects.isEmpty) {
            null
          } else {
            val projectId = projects.next.get(Project.Fields.id.toString())
            Project().copy(id = Some(projectId.asInstanceOf[ObjectId]))
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
          tasks.asInstanceOf[BasicDBList].toScalaList.contains(id.get)
        }
      }
    }
  }

}
