package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import com.mongodb.casbah.Imports.$set
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.commons._

case class Project(
  id: Option[ObjectId],
  name: String,
  version: Int,
  boards: Option[List[Board]]) extends HasMongoConnection with HasMidAirCollisionDetection with Equals {

  def store: Project = {
    val idToUpdate = id.getOrElse({
      val obj = Project.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.Projects) += obj
      }
      return Project.asEntity(obj)
    })

    val update = $set(
      Project.Fields.name.toString -> name,
      Project.Fields.version.toString -> { version + 1 },
      Project.Fields.boards.toString -> Project.toIdList[Board](boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))))

    Project.asEntity(versionedUpdate(Coll.Projects, versionedQuery(idToUpdate, version), update))
  }

  def delete() {
    versionedDelete(Coll.Projects, versionedQuery(id.get, version))
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[com.googlecode.kanbanik.model.Project]
  }

  override def equals(other: Any) = {
    other match {
      case that: Project => that.canEqual(Project.this) && id == that.id
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + id.hashCode
  }

}

object Project extends HasMongoConnection {

  object Fields extends DocumentField {
    val boards = Value("boards")
    val tasks = Value("tasks")
  }

  def allForBoard(board: Board): List[Project] = {
    using(createConnection) { conn =>
      coll(conn, Coll.Projects).find(MongoDBObject(Fields.boards.toString -> board.id.get)).map(asEntity(_, board)).toList
    }
  }

  def all(): List[Project] = {
    using(createConnection) { conn =>
      coll(conn, Coll.Projects).find().sort(MongoDBObject(Fields.name.toString -> 1)).map(asEntity).toList
    }
  }

  def byId(id: ObjectId): Project = {
    using(createConnection) { conn =>
      val dbProject = coll(conn, Coll.Projects).findOne(MongoDBObject(Fields.id.toString -> id)).getOrElse(throw new IllegalArgumentException("No such project with id: " + id))
      asEntity(dbProject)
    }
  }

  def asDBObject(entity: Project): DBObject = {
    MongoDBObject(
      Fields.id.toString -> new ObjectId,
      Fields.name.toString -> entity.name,
      Fields.version.toString -> entity.version,
      Fields.boards.toString -> toIdList[Board](entity.boards, _.id.getOrElse(throw new IllegalArgumentException("The board has to exist!"))))
  }

  private def asEntity(dbObject: DBObject, board: Board): Project = {
    asEntity(dbObject, obj => Some(List(board)))
  }

  private def asEntity(dbObject: DBObject): Project = {
    val allBoards = buildMap[Board](Board.all(includeTasks = false))
    asEntity(dbObject, obj => toEntityList(obj.get(Fields.boards.toString), allBoards.get(_).get))
  }

  private def asEntity(dbObject: DBObject, boardsProvider: DBObject => Option[List[Board]]): Project = {
    new Project(
      Some(dbObject.get(Fields.id.toString).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString).asInstanceOf[String],
      dbObject.getWithDefault[Int](Fields.version, 1),
      boardsProvider(dbObject)
    )
  }

  def buildMap[T <: { def id: Option[ObjectId] }](allEntities: List[T]): Map[ObjectId, T] = {
    val zipped = allEntities.map(_.id.get) zip allEntities
    zipped.toMap
  }

  private def toIdList[T](entities: Option[List[T]], codeBlock: T => ObjectId) = {
    if (!entities.isDefined) {
      null
    } else {
      for { entity <- entities.get } yield codeBlock(entity)
    }
  }

  private def toEntityList[T](dbObject: Object, codeBlock: ObjectId => T) = {
    if (dbObject == null || dbObject == None) {
      None
    } else {

      val processList: List[ObjectId] = {
        dbObject match {
          case ids: List[ObjectId] =>
            ids
          case _ =>
            dbObject.asInstanceOf[BasicDBList].toArray.toList.asInstanceOf[List[ObjectId]]
        }
      }

      if (processList == null || processList.size == 0) {
        None
      } else {
        Some(for { boardId <- processList } yield codeBlock(boardId))
      }
    }
  }

  def apply() = new Project(Some(new ObjectId()), "", -1, None)
}