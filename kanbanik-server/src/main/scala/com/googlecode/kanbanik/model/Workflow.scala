package com.googlecode.kanbanik.model

import com.mongodb.DBObject
import org.bson.types.ObjectId
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.BasicDBList

class Workflow(
  val id: Option[ObjectId],
  val workflowitems: List[Workflowitem]) extends HasMongoConnection {

  def addItem(item: Workflowitem, nextItem: Option[Workflowitem], destWorkflow: Workflow): Workflow = {

    def addInThisWorkflow = {
      if (!nextItem.isDefined) {
        new Workflow(id, workflowitems ++ List(item))
      } else {
        val indexOfNext = workflowitems indexOf (nextItem.get)
        val added = workflowitems.take(indexOfNext) ++ List(item) ++ workflowitems.drop(indexOfNext)
        new Workflow(id, added)
      }
    }

    def addInInnerWorkflow(inItems: List[Workflowitem]): List[Workflowitem] = {
      inItems match {
        case Nil => Nil
        case x :: xs => {
          x.withWorkflow(x.nestedWorkflow.addItem(item, nextItem, destWorkflow)) :: addInInnerWorkflow(xs)
        }
      }
    }

    if (destWorkflow == this) {
      addInThisWorkflow
    } else {
      new Workflow(id, addInInnerWorkflow(workflowitems))
    }

  }

  def addItem(item: Workflowitem, nextItem: Option[Workflowitem]): Workflow = addItem(item, nextItem, this)

  def removeItem(item: Workflowitem): Workflow = {
    def removeItem(items: List[Workflowitem]): List[Workflowitem] = {
      items match {
        case Nil => Nil
        case x :: xs =>
          if (x == item) xs
          else
            x.withWorkflow(new Workflow(
              x.nestedWorkflow.id,
              removeItem(x.nestedWorkflow.workflowitems))) :: removeItem(xs)
      }
    }

    new Workflow(id, removeItem(workflowitems))
  }

  def withWorkflowitems(workflowitems: List[Workflowitem]) =
    new Workflow(id, workflowitems)

  def asDbObject(): DBObject = {
    MongoDBObject(
      Workflow.Fields.id.toString() -> id.getOrElse(new ObjectId),
      Workflow.Fields.workflowitems.toString() -> workflowitems.map(_.asDbObject))
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[com.googlecode.kanbanik.model.Workflow]
  }

  override def equals(other: Any) = {
    other match {
      case that: Workflow => that.canEqual(Workflow.this) && id == that.id
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + id.hashCode
  }

  //  def asDBObject() = Workflow.asDBObject(this)
}

object Workflow extends HasMongoConnection {

  object Fields extends DocumentField {
    val workflowitems = Value("workflowitems")
  }

  def apply() = new Workflow(Some(new ObjectId()), List())
  def apply(items: List[Workflowitem]) = new Workflow(Some(new ObjectId()), items)
  def apply(id: ObjectId, items: List[Workflowitem]) = new Workflow(Some(id), items)

  //  def byId(id: ObjectId): Workflow = {
  //    using(createConnection) { conn =>
  //      val dbWorkflow = coll(conn, Coll.Workflow).findOne(MongoDBObject(Fields.id.toString() -> id)).getOrElse(throw new IllegalArgumentException("No such workflow with id: " + id))
  //      asEntity(dbWorkflow)
  //    }
  //  }
  //
  def asEntity(dbObject: DBObject): Workflow = {

    new Workflow(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      {
        val list = dbObject.get(Fields.workflowitems.toString()).asInstanceOf[List[DBObject]]
        list.map(Workflowitem.asEntity(_))
      })
  }

}