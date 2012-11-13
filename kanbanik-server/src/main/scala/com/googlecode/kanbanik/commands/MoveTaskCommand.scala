package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.MoveTaskParams
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages

class MoveTaskCommand extends ServerCommand[MoveTaskParams, FailableResult[SimpleParams[TaskDto]]] with TaskManipulation {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: MoveTaskParams): FailableResult[SimpleParams[TaskDto]] = {
	try {
	  Task.byId(new ObjectId(params.getTask().getId()))
	} catch {
		case e: IllegalArgumentException =>
	  		return new FailableResult(new SimpleParams(params.getTask()), false, ServerMessages.entityDeletedMessage("task"))
	}
	  
    val task = taskBuilder.buildEntity(params.getTask())
    val project = Project.byId(new ObjectId(params.getProject().getId()));

    
    val definedOnProject = findProjectForTask(task).getOrElse(throw new IllegalStateException("The task '" + task.id + "' is defined on NO project!"))

    if (project.id == definedOnProject.id) {
      try {
 	    return new FailableResult(new SimpleParams(taskBuilder.buildDto(task.store)))
      } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(taskBuilder.buildDto(task)), false, ServerMessages.midAirCollisionException)
      }
    }

    try {
    	val resTask = task.store
	    removeTaskFromProject(resTask, definedOnProject)
	    addTaskToProject(resTask, project)
	    return new FailableResult(new SimpleParams(taskBuilder.buildDto(resTask)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(taskBuilder.buildDto(task)), false, ServerMessages.midAirCollisionException)
    }
    
  }
}