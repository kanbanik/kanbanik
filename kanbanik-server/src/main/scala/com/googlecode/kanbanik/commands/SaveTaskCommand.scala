package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Project
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.model.MidAirCollisionException
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId

class SaveTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[SimpleParams[TaskDto]]] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): FailableResult[SimpleParams[TaskDto]] = {
    if (params.getPayload().getWorkflowitem() == null) {
      return new FailableResult(null, false, "At least one workflowitem must exist to create a task!")
    }
    
    try {
    	Workflowitem.byId(new ObjectId(params.getPayload().getWorkflowitem().getId))
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, "The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data.")
    }
    
    val task = taskBuilder.buildEntity(params.getPayload())
    val isNew = !task.id.isDefined
    
    var storedTask: Task = null
    try {
    	storedTask = task.store
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(params.getPayload()), false, ServerMessages.midAirCollisionException)
    }
    
    val project = Project.byId(new ObjectId(params.getPayload().getProject().getId()))
    
    if (isNew) {
    	addTaskToProject(storedTask, project)
    }

    return new FailableResult(new SimpleParams(taskBuilder.buildDto(storedTask)))
  }
}