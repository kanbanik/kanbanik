package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.HasMongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.model.Task

class DeleteWorkflowitemCommand extends ServerCommand[SimpleParams[WorkflowitemDto], FailableResult[VoidParams]] with HasMongoConnection {
  
  lazy val workflowitemBuilder = new WorkflowitemBuilder
  
  def execute(params: SimpleParams[WorkflowitemDto]): FailableResult[VoidParams] = {

    val id = new ObjectId(params.getPayload().getId())
    
    if (hasTasksOnWorkflowitem(id)) {
      return new FailableResult(new VoidParams, false, "This workflowitem can not be deleted, because there are tasks associated with this workflowitem.")
    }
    
    if (Workflowitem.byId(id).child.isDefined) {
      return new FailableResult(new VoidParams, false, "This workflowitem can not be deleted, because it has a child workflowitem.")
    }
    
    val entity = workflowitemBuilder.buildEntity(params.getPayload())
    entity.delete
    
    return new FailableResult(new VoidParams, true, "")
  }
  
  def hasTasksOnWorkflowitem(workflowitemId: ObjectId): Boolean = {
    using(createConnection) { conn =>
      return coll(conn, Coll.Tasks).findOne(MongoDBObject(Task.Fields.workflowitem.toString() -> workflowitemId)).isDefined
    }
  }
}