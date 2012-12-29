package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.model.Board

class DeleteTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[VoidParams]] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): FailableResult[VoidParams] = {
    
    val boardId = new ObjectId(params.getPayload().getWorkflowitem().getParentWorkflow().getBoard().getId)
    val workflowitemId = new ObjectId(params.getPayload().getWorkflowitem().getId)
    
    val board = Board.byId(boardId)
    board.workflow.containsItem(Workflowitem().withId(workflowitemId))

    if (!board.workflow.containsItem(Workflowitem().withId(workflowitemId))) {
      return new FailableResult(new VoidParams, false, "The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data.")
    }
    
    val task = taskBuilder.buildEntity(params.getPayload())
    
    val project = findProjectForTask(task).getOrElse(return new FailableResult(new VoidParams(), false, "The task is defined on no project - it has possibly been deleted by a different user. Please refresh your browser to get the current data."))
    
    try {
    	task.delete
    } catch {
      case e: MidAirCollisionException =>
        	return new FailableResult(new VoidParams(), false, ServerMessages.midAirCollisionException)
    }

    // well... if this goes wrong, the DB will end up in an inconsistent state - but it should be fast enough that this will never happen
    removeTaskFromProject(task, project)
    
    new FailableResult(new VoidParams)
  }
}