package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Project
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board

class SaveTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[SimpleParams[TaskDto]]] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): FailableResult[SimpleParams[TaskDto]] = {
    if (params.getPayload().getWorkflowitem() == null) {
      return new FailableResult(null, false, "At least one workflowitem must exist to create a task!")
    }
    
    try {
      val boardId = params.getPayload().getWorkflowitem().getParentWorkflow().getBoard().getId()
      val board = Board.byId(new ObjectId(boardId))
      val workdlowitemId = new ObjectId(params.getPayload().getWorkflowitem().getId())
      board.workflow.findItem(Workflowitem().withId(workdlowitemId)).getOrElse(throw new IllegalArgumentException())
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, "The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data.")
    }
    
    val task = taskBuilder.buildEntity(params.getPayload())
    val isNew = !task.id.isDefined
    
    try {
    	val stored = task.store
    	return new FailableResult(new SimpleParams(taskBuilder.buildDto(stored)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, ServerMessages.midAirCollisionException)
    }
    
  }
}