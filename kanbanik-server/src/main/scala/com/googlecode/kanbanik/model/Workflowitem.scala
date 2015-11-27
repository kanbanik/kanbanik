package com.googlecode.kanbanik.model

import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dtos.WorkflowitemType
import com.googlecode.kanbanik.commons._

case class Workflowitem(
  id: Option[ObjectId],
  name: String,
  wipLimit: Int,
  verticalSize: Int,
  itemType: String,
  version: Int,
  nestedWorkflow: Workflow,
  _parentWorkflow: Option[Workflow])
  extends HasMongoConnection
  with HasMidAirCollisionDetection with Equals {

  def this(id: Option[ObjectId],
    name: String,
    wipLimit: Int,
    verticalSize: Int,
    itemType: String,
    version: Int,
    nestedWorkflow: Workflow) = this(id, name, wipLimit, verticalSize, itemType, version, nestedWorkflow, None)

  def parentWorkflow(user: User): Workflow = _parentWorkflow.getOrElse(loadWorkflow(user))

  private def loadWorkflow(user: User): Workflow = {
    val parentBoard = Board.all(includeTasks = false, user).find(board => board.workflow.containsItem(this)).getOrElse(throw new IllegalArgumentException("The workflowitem '" + id + "' does not exist on any board!"))
    parentBoard.workflow.findItem(this).get.parentWorkflow(user)
  }

  def asDbObject(): DBObject = {
    MongoDBObject(
      Workflowitem.Fields.id.toString -> id.getOrElse(new ObjectId),
      Workflowitem.Fields.name.toString -> name,
      Workflowitem.Fields.wipLimit.toString -> wipLimit,
      Workflowitem.Fields.verticalSize.toString -> verticalSize,
      Workflowitem.Fields.itemType.toString -> itemType,
      Workflowitem.Fields.version.toString -> version,
      Workflowitem.Fields.nestedWorkflow.toString -> nestedWorkflow.asDbObject)
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[com.googlecode.kanbanik.model.Workflowitem]
  }

  override def equals(other: Any) = {
    other match {
      case that: Workflowitem => that.canEqual(Workflowitem.this) && id == that.id
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + id.hashCode
  }

  override def toString = id.toString

}

object Workflowitem extends HasMongoConnection {

  object Fields extends DocumentField {
    val wipLimit = Value("wipLimit")
    val verticalSize = Value("verticalSize")
    val itemType = Value("itemType")
    val nestedWorkflow = Value("nestedWorkflow")
  }

  def apply() = new Workflowitem(Some(new ObjectId()), "", -1, -1, WorkflowitemType.HORIZONTAL.toString, 1, Workflow(), None)

  def apply(id: String) = new Workflowitem(Some(new ObjectId(id)), "", -1, -1, WorkflowitemType.HORIZONTAL.toString, 1, Workflow(), None)

  def apply(id: ObjectId) = new Workflowitem(Some(id), "", -1, -1, WorkflowitemType.HORIZONTAL.toString, 1, Workflow(), None)

  def apply(id: String, parent: Workflow) =
    new Workflowitem(
      Some(new ObjectId(id)),
      "",
      -1,
      -1,
      WorkflowitemType.HORIZONTAL.toString,
      1,
      Workflow(),
      Some(parent)
    )


  def asEntity(dbObject: DBObject, workflow: Option[Workflow]): Workflowitem = {
    new Workflowitem(
      Some(dbObject.get(Fields.id.toString).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString).asInstanceOf[String],
      dbObject.get(Fields.wipLimit.toString).asInstanceOf[Int],
      dbObject.getWithDefault[Int](Fields.verticalSize, -1),
      dbObject.get(Fields.itemType.toString).asInstanceOf[String],
      dbObject.get(Fields.version.toString).asInstanceOf[Int],
      {
        val nestedWorkflow = dbObject.get(Fields.nestedWorkflow.toString).asInstanceOf[DBObject]
        Workflow.asEntity(nestedWorkflow)
      },
      workflow)
  }

  def asEntity(dbObject: DBObject): Workflowitem = {
    asEntity(dbObject, None)
  }

}