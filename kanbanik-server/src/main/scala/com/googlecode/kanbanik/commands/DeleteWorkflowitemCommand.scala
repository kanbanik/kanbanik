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
import com.googlecode.kanbanik.db.HasEntityLoader

class DeleteWorkflowitemCommand extends ServerCommand[SimpleParams[WorkflowitemDto], FailableResult[VoidParams]] with HasMongoConnection with HasEntityLoader {
  
  lazy val workflowitemBuilder = new WorkflowitemBuilder
  
  lazy val boardBuilder = new BoardBuilder
  
  def execute(params: SimpleParams[WorkflowitemDto]): FailableResult[VoidParams] = {

    val theId = new ObjectId(params.getPayload().getId())
    
    val board = loadBoard(new ObjectId(params.getPayload().getParentWorkflow().getBoard().getId), true).getOrElse(
        return new FailableResult(new VoidParams, false, ServerMessages.entityDeletedMessage("board " + params.getPayload().getParentWorkflow().getBoard().getName()))
    )
    
    val item = Workflowitem().copy(id = Some(theId))
    
    try {
    	board.workflow.containsItem(item)
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.entityDeletedMessage("workflowitem"))
    }
    
    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitem == Workflowitem().copy(id = Some(theId)))
    if (tasksOnWorkflowitem.size != 0) {
      val ticketIds = tasksOnWorkflowitem.map(_.ticketId).mkString(", ")
      return new FailableResult(new VoidParams, false, "This workflowitem can not be deleted, because there are tasks associated with this workflowitem. Tasks: [" + ticketIds + "]")
    }
    
    val foundItem = board.workflow.findItem(item)
    if (foundItem.isDefined && foundItem.get.nestedWorkflow.workflowitems.size > 0) {
      return new FailableResult(new VoidParams, false, "This workflowitem can not be deleted, because it has a nested workflow. Please delete it first")
    }

    try {
    	board.copy(workflow = board.workflow.removeItem(foundItem.getOrElse(
    			return new FailableResult(new VoidParams, false, "This workflowitem has been deleted by a different user. Please refresh your browser to get the current data")
    	))).store
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new VoidParams, false, ServerMessages.midAirCollisionException)
    }
    
    return new FailableResult(new VoidParams, true, "")
  }
}
