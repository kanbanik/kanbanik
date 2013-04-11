package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.Project
import org.bson.types.ObjectId
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.validation.ProjectValidation
import com.googlecode.kanbanik.db.HasEntityLoader

class DeleteProjectCommand extends ServerCommand[SimpleParams[ProjectDto], FailableResult[VoidParams]] with ProjectValidation with HasEntityLoader {
  
  val projectBuilder = new ProjectBuilder
  
  def execute(params: SimpleParams[ProjectDto]): FailableResult[VoidParams] = {
    
    loadProject(new ObjectId(params.getPayload().getId())).getOrElse(return new FailableResult(new VoidParams(), false, ServerMessages.entityDeletedMessage("project")))
    
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