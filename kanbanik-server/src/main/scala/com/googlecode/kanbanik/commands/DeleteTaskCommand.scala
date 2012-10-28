package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.model.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages

class DeleteTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[VoidParams]] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): FailableResult[VoidParams] = {
    val task = taskBuilder.buildEntity(params.getPayload())
    
    val project = findProjectForTask(task).getOrElse(return new FailableResult(new VoidParams(), false, "The task is defined on no project - it has possibly been deleted by a different user. Please refresh your browser to get the accurate data."))
    
    try {
    	task.delete
    } catch {
      case e: MidAirCollisionException =>
        	return new FailableResult(new VoidParams(), false, ServerMessages.midAirCollisionException)
    }

    // well... if this goes wrong, the DB will end up in an inconsistent state - but it should be fast enough that this will never happen
    removeTaskFromProject(task, project)
    
    new FailableResult(new VoidParams())
  }
}