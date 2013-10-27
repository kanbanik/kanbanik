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
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.commons._

class DeleteTasksCommand extends ServerCommand[SimpleParams[ListDto[TaskDto]], FailableResult[VoidParams]] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[ListDto[TaskDto]]): FailableResult[VoidParams] = {
	  
    val paramsList = params.getPayload().getList().toScalaList
	  
	  // I'm too close to the release so there is no time doing it properly
	  // TODO try to find a way how to not bomb the DB with every task
	  val results = paramsList.par.map(doExecute(_))
	  val errorResults = results.filter(!_.isSucceeded())
	  if (errorResults.isEmpty) {
	    new FailableResult(new VoidParams)
	  } else {
	    val messages = errorResults.map(_.getMessage())
	    return new FailableResult(new VoidParams, false, messages.mkString(", "))
	  }
  }
  
  private def doExecute(taskDto: TaskDto): FailableResult[VoidParams] = {
    val boardId = new ObjectId(taskDto.getWorkflowitem().getParentWorkflow().getBoard().getId)
    val workflowitemId = new ObjectId(taskDto.getWorkflowitem().getId)
    
    val board = Board.byId(boardId, false)
    board.workflow.containsItem(Workflowitem().copy(id = Some(workflowitemId)))

    if (!board.workflow.containsItem(Workflowitem().copy(id = Some(workflowitemId)))) {
      return new FailableResult(new VoidParams, false, "The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data.")
    }
    
    val task = taskBuilder.buildEntity(taskDto)
    
    val project = Project.byId(task.project.id.get)
      
    try {
    	task.delete(boardId)
    } catch {
      case e: MidAirCollisionException =>
        	return new FailableResult(new VoidParams(), false, ServerMessages.midAirCollisionException)
    }

    new FailableResult(new VoidParams)    
  }
}