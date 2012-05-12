package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import com.googlecode.kanbanik.model.KanbanikEntity
import com.mongodb.casbah.commons.MongoDBObject

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, VoidParams] with KanbanikEntity {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  def execute(params: EditWorkflowParams): VoidParams = {

    val parentDto = params.getParent()
    val currenDto = params.getCurrent()
    val nextOfCurrentDto = params.getCurrent().getNextItem()

    if (currenDto.getId() != null) {
      val prevCurrent = WorkflowitemScala.byId(new ObjectId(currenDto.getId()))
      val prevParent = findParent(prevCurrent)
      if (prevParent.isDefined) {
        prevParent.get.child = prevCurrent.nextItem
        prevParent.get.store
      }
    }

    val currentEntity = workflowitemBuilder.buildEntity(currenDto)

    currentEntity.store

    if (parentDto != null) {
      val parentEntity = workflowitemBuilder.buildEntity(parentDto)
      parentEntity.child = Some(currentEntity)
      parentEntity.store
    }

    new VoidParams
  }

  def findParent(child: WorkflowitemScala): Option[WorkflowitemScala] = {
    using(createConnection) { conn =>
      val maxTaskId = coll(conn, Coll.Workflowitems).find()
      val parent = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("childId" -> child.id))

      if (parent.isDefined) {
        return Some(WorkflowitemScala.byId(parent.get.get("_id").asInstanceOf[ObjectId]));
      } else {
        return None
      }

    }
  }

}