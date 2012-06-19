package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.ProjectScala
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.FailableResult

class SaveTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[SimpleParams[TaskDto]]] with TaskManipulation {
  
  private lazy val taskBuilder = new TaskBuilder()
  
  def execute(params: SimpleParams[TaskDto]): FailableResult[SimpleParams[TaskDto]] = {
    if (params.getPayload().getWorkflowitem() == null) {
      return new FailableResult(null, false, "At least one workflowitem must exist to create a task!")
    }
    
    val task = taskBuilder.buildEntity(params.getPayload())
    val isNew = !task.id.isDefined
    
    val storedTask = task.store
    val project = ProjectScala.byId(new ObjectId(params.getPayload().getProject().getId()))
    
    if (isNew) {
    	addTaskToProject(storedTask, project)
    }

    return new FailableResult(new SimpleParams(taskBuilder.buildDto(storedTask)), true, "")
  }
}