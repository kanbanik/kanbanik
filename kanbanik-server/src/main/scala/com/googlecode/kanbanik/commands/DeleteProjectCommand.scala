package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.Project
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.validation.ProjectValidation
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.MidAirCollisionException
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.messages.ServerMessages

class DeleteProjectCommand extends ServerCommand[SimpleParams[ProjectDto], FailableResult[VoidParams]] with ProjectValidation {
  
  val projectBuilder = new ProjectBuilder
  
  def execute(params: SimpleParams[ProjectDto]): FailableResult[VoidParams] = {
    
    try {
    	Project.byId(new ObjectId(params.getPayload().getId()))
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.entityDeletedMessage("project"))
    }
    
    val project = projectBuilder.buildEntity(params.getPayload())
    
    val (deletable, msg) = canBeDeleted(project)
    if (!deletable) {
    	return new FailableResult(new VoidParams, false, msg)
    }
    
    try {
    	project.delete
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new VoidParams(), false, ServerMessages.midAirCollisionException)
    }
    
    return new FailableResult(new VoidParams)
  }
}