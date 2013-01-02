package com.googlecode.kanbanik.commands;
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.builders.BoardBuilder

class EditWorkflowitemDataCommand extends ServerCommand[SimpleParams[WorkflowitemDto], FailableResult[SimpleParams[WorkflowitemDto]]] with HasMongoConnection {
  
  val workflowitemBuilder = new WorkflowitemBuilder
  
  val boardBuilder = new BoardBuilder
  
  def execute(params: SimpleParams[WorkflowitemDto]): FailableResult[SimpleParams[WorkflowitemDto]] = {
    val builtBoard = boardBuilder.buildEntity(params.getPayload().getParentWorkflow().getBoard())
    val workflowitem = workflowitemBuilder.buildEntity(params.getPayload(), None, Some(builtBoard))
    
    try {
    	val storedBoard = builtBoard.store	
    	new FailableResult(new SimpleParams(workflowitemBuilder.buildDto(storedBoard.workflow.findItem(workflowitem).get, None)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, ServerMessages.midAirCollisionException)
    }
  }
}
