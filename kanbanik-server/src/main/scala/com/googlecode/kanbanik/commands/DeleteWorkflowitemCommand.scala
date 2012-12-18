package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.exceptions.ResourceLockedException
import com.googlecode.kanbanik.builders.BoardBuilder

class DeleteWorkflowitemCommand extends ServerCommand[SimpleParams[WorkflowitemDto], FailableResult[VoidParams]] with HasMongoConnection {
  
  lazy val workflowitemBuilder = new WorkflowitemBuilder
  
  lazy val boardBuilder = new BoardBuilder
  
  def execute(params: SimpleParams[WorkflowitemDto]): FailableResult[VoidParams] = {

    val id = new ObjectId(params.getPayload().getId())
    
    try {
    	Workflowitem.byId(id)
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.entityDeletedMessage("workflowitem"))
    }
    
    if (hasTasksOnWorkflowitem(id)) {
      return new FailableResult(new VoidParams, false, "This workflowitem can not be deleted, because there are tasks associated with this workflowitem.")
    }
    
    if (Workflowitem.byId(id).nestedWorkflow.workflowitems.size > 0) {
      return new FailableResult(new VoidParams, false, "This workflowitem can not be deleted, because it has a nested workflow. Please delete it first")
    }

    try {
    	Board.byId(new ObjectId(params.getPayload().getParentWorkflow().getBoard().getId()))
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new VoidParams, false, ServerMessages.entityDeletedMessage("board " + params.getPayload().getParentWorkflow().getBoard().getName()))
    }

    val currentBoard = boardBuilder.buildEntity(params.getPayload().getParentWorkflow().getBoard())
    
   
    try {
    	val workflowitem = workflowitemBuilder.buildEntity(params.getPayload(), None, None)
    	val board = workflowitem.parentWorkflow.board
    	workflowitem.delete
    	board.withWorkflow(board.workflow.removeItem(workflowitem)).store
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new VoidParams, false, ServerMessages.midAirCollisionException)
    }
    
    return new FailableResult(new VoidParams, true, "")
  }
  
  def hasTasksOnWorkflowitem(workflowitemId: ObjectId): Boolean = {
    using(createConnection) { conn =>
      return coll(conn, Coll.Tasks).findOne(MongoDBObject(Task.Fields.workflowitem.toString() -> workflowitemId)).isDefined
    }
  }
}