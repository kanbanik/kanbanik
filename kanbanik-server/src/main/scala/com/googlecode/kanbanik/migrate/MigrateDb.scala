package com.googlecode.kanbanik.migrate

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.mongodb.DBObject
import com.googlecode.kanbanik.model._
import org.bson.types.ObjectId
import com.mongodb.BasicDBList
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.dtos.{WorkfloVerticalSizing, ManipulateUserDto}


class MigrateDb extends HasMongoConnection {

  val versionMigrations = Map(
    1 -> List(new From1To2, new From2To3, new From3To4),
    2 -> List(new From2To3, new From3To4),
    3 -> List(new From3To4)
  )

  def migrateDbIfNeeded {
    System.out.println("migration started")
    using(createConnection) {
      conn =>
        val version = coll(conn, Coll.KanbanikVersion).findOne()
        if (version.isDefined) {
          val curVersion = version.get.get("version").asInstanceOf[Int]
          System.out.println("version defined and is: " + curVersion)
          runAllFrom(curVersion)
        } else {
          System.out.println("version is 1")
          coll(conn, Coll.KanbanikVersion) += MongoDBObject("version" -> 1)
          runAllFrom(1)
        }

    }
  }

  def runAllFrom(curVersion: Int) {
    System.out.println("running all from: " + curVersion)
    val migrationParts = versionMigrations.get(curVersion)
    if (migrationParts.isDefined) {
      for (part <- migrationParts.get) {
        System.out.println("START migration using " + part.getClass)
        part.migrate
        System.out.println("END migration using " + part.getClass)
      }
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

// from 0.2.3 -> 0.2.4
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

// from 0.2.4 -> 0.2.5
class From2To3 extends MigrationPart {
  private val oldTasksCollection = "tasks"
  private val boardsCollection = "boards"

  private lazy val taskBuilder = new TaskBuilder()

  def migrate {

    val classesOfServices = createDefaultClassesOfService()

    migrateBoard

    migrateTasks(classesOfServices)

    deleteOldTasks()

    setVersionTo(3)

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

  def deleteOldTasks() {
    using(createConnection) {
      conn =>
        coll(conn, oldTasksCollection).remove(MongoDBObject())
    }
  }

  case class NewTask(id: Option[ObjectId],
                     name: String,
                     description: String,
                     classOfService: Option[ClassOfService],
                     ticketId: String,
                     version: Int,
                     order: String,
                     assignee: Option[User],
                     dueData: String,
                     workflowitem: Workflowitem,
                     project: Project) {

    object Fields extends DocumentField {
      val description = Value("description")
      val ticketId = Value("ticketId")
      val order = Value("order")
      val projectId = Value("projectId")
      val workflowitem = Value("workflowitem")
      val classOfService = Value("classOfService")
      val assignee = Value("assignee")
      val dueDate = Value("dueDate")
    }

    def asDBObject(entity: NewTask): DBObject = {

      MongoDBObject(
        Fields.id.toString() -> {
          if (entity.id == null || !entity.id.isDefined) new ObjectId else entity.id
        },
        Fields.name.toString() -> entity.name,
        Fields.description.toString() -> entity.description,
        Fields.classOfService.toString() -> entity.classOfService,
        Fields.ticketId.toString() -> entity.ticketId,
        Fields.version.toString() -> entity.version,
        Fields.order.toString() -> entity.order,
        Fields.projectId.toString() -> entity.project.id,
        Fields.classOfService.toString() -> {
          if (entity.classOfService.isDefined) entity.classOfService.get.id else None
        },
        Fields.assignee.toString() -> {
          if (entity.assignee.isDefined) entity.assignee.get.name else None
        },
        Fields.dueDate.toString() -> entity.dueData,
        Fields.workflowitem.toString() -> entity.workflowitem.id.getOrElse(throw new IllegalArgumentException("Task can not exist without a workflowitem")))
    }

    def store() {
      using(createConnection) {
        conn =>
          val update = $push(Coll.Tasks.toString() -> asDBObject(this))
          coll(conn, Coll.Boards).findAndModify(MongoDBObject(Fields.id.toString() -> workflowitem.parentWorkflow.board.id.get), null, null, false, update, true, false)
      }
    }

  }

  class OldTask(val id: Option[ObjectId],
                val name: String,
                val description: String,
                val classOfService: Int,
                val ticketId: String,
                val version: Int,
                val workflowitemId: ObjectId) {

    def asNewTask(classesOfService: Map[Int, ClassOfService]): NewTask = {
      new NewTask(
      None, // because I want to create a new one
      name,
      description, {
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
      findProject()
      )
    }

    def findWorkflowitem(): Workflowitem = {
      val board = Board.all(false).find(board => board.workflow.containsItem(Workflowitem().copy(id = Some(workflowitemId)))).getOrElse(return null)
      val workflowitem = board.workflow.findItem(Workflowitem().copy(id = Some(workflowitemId)))
      workflowitem.orNull
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
        if (tasks.isInstanceOf[List[_]]) {
          tasks.asInstanceOf[List[_]].contains(id.get)
        } else {
          tasks.asInstanceOf[BasicDBList].toScalaList.contains(id.get)
        }
      }
    }
  }
}

// from 0.2.5 -> 0.2.6
class From3To4 extends MigrationPart {
  def migrate {
    using(createConnection) {
      conn => {
        for (board <- coll(conn, "boards").find()) {
          val tasks = board.get("tasks")
          if (tasks != null && tasks.isInstanceOf[BasicDBList]) {
            val list = board.get("tasks").asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[DBObject]]
            val oldTaskIds = list.map(_.get(Fields.id.toString))

            // create new tasks
            for (task <- list) {
              asNewTask(task, board.get(Fields.id.toString()).asInstanceOf[ObjectId]).store
            }

            // delete old tasks
            for (oldId <- oldTaskIds) {
              val update = $pull(Coll.Tasks.toString() -> MongoDBObject(Fields.id.toString() -> oldId.asInstanceOf[ObjectId]))
              coll(conn, Coll.Boards).update(MongoDBObject(Board.Fields.id.toString() -> board.get(Fields.id.toString()).asInstanceOf[ObjectId]), update)
            }
          }
        }

      }
    }

    setVersionTo(4)
  }

  object Fields extends DocumentField {
    val description = Value("description")
    val ticketId = Value("ticketId")
    val order = Value("order")
    val projectId = Value("projectId")
    val workflowitem = Value("workflowitem")
    val classOfService = Value("classOfService")
    val assignee = Value("assignee")
    val dueDate = Value("dueDate")
    val boardId = Value("boardId")
  }

  def asNewTask(dbObject: DBObject, boardId: ObjectId): Task = {
    new Task(
      None,
      dbObject.get(Fields.name.toString()).asInstanceOf[String],
      dbObject.get(Fields.description.toString()).asInstanceOf[String],
      loadOrNone[ObjectId, ClassOfService](Fields.classOfService.toString(), dbObject, loadClassOfService(_)),
      dbObject.get(Fields.ticketId.toString()).asInstanceOf[String],
      {
        val version = dbObject.getWithDefault[Int](Fields.version, 1)
        if (version != 0) {
          version
        } else {
          1
        }
      },
      dbObject.get(Fields.order.toString()).asInstanceOf[String],
      loadOrNone[String, User](Fields.assignee.toString(), dbObject, loadUser(_)),
      dbObject.getWithDefault[String](Fields.dueDate, ""),
      dbObject.get(Fields.workflowitem.toString()).asInstanceOf[ObjectId],
      boardId,
      dbObject.get(Fields.projectId.toString()).asInstanceOf[ObjectId],
      None
    )

  }

  def loadOrNone[T, R](dbField: String, dbObject: DBObject, f: T => Option[R]): Option[R] = {
    val res = dbObject.get(dbField)
    if (res == null) {
      None
    } else {
      f(res.asInstanceOf[T])
    }
  }

  def loadClassOfService(id: ObjectId) = {
    try {
      Some(ClassOfService.byId(id))
    } catch {
      case e: IllegalArgumentException =>
        None
    }

  }

  def loadUser(name: String) = {
    try {
      Some(User.byId(name))
    } catch {
      case e: IllegalArgumentException =>
        None
    }

  }

}
