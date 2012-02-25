package com.googlecode.kanbanik
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import com.mongodb.BasicDBList

class WorkflowitemScala(
  var id: Option[String],
  var name: String,
  var wipLimit: Int,
  var children: Option[List[WorkflowitemScala]],
  var nextItemId: Option[String])
  extends KanbanikEntity {

  def store: WorkflowitemScala = {
    val obj = WorkflowitemScala.asDBObject(this)
    coll(Coll.Workflowitems) += obj 
    WorkflowitemScala.asEntity(obj)
  }

}

object WorkflowitemScala extends KanbanikEntity {
  def byId(id: String): WorkflowitemScala = {
    val dbWorkflow = coll(Coll.Workflowitems).findOne(MongoDBObject("_id" -> new ObjectId(id))).getOrElse(throw new IllegalArgumentException("No such workflowitem with id: " + id))
    asEntity(dbWorkflow)
  }

  def asEntity(dbObject: DBObject) = {
    val children = dbObject.get("children")
    var toStore : Option[List[WorkflowitemScala]] = None
    if (children.isInstanceOf[BasicDBList]) {
      toStore = translateChildren(children.asInstanceOf[BasicDBList])
    }
    
    new WorkflowitemScala(
      Some(dbObject.get("_id").asInstanceOf[ObjectId].toString()),
      dbObject.get("name").asInstanceOf[String],
      dbObject.get("wipLimit").asInstanceOf[Int],
      toStore,
      None)
  }

  def asDBObject(entity: WorkflowitemScala): DBObject = {
    MongoDBObject(
      "_id" -> new ObjectId(),
      "name" -> entity.name,
      "wipLimit" -> entity.wipLimit,
      "children" -> translateChildren(entity.children),
      "nextItemId" -> null)
  }

  def translateChildren(children: BasicDBList): Option[List[WorkflowitemScala]] = {
    if (children == null) {
      None
    } else {
      Some(for { x <- children.toArray().toList } yield asEntity(x.asInstanceOf[DBObject]))
    }

  }

  def translateChildren(children: Option[List[WorkflowitemScala]]): List[DBObject] = {
    if (!children.isDefined) {
      null
    } else {
      for { x <- children.get } yield asDBObject(x)
    }

  }
}