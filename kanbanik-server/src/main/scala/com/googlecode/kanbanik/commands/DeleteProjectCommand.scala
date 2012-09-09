package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.Project
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.validation.ProjectValidation

class DeleteProjectCommand extends ServerCommand[SimpleParams[ProjectDto], FailableResult[VoidParams]] with ProjectValidation {
  def execute(params: SimpleParams[ProjectDto]): FailableResult[VoidParams] = {
    val project = Project.byId(new ObjectId(params.getPayload().getId()))
    val (deletable, msg) = canBeDeleted(project)
    if (!deletable) {
    	return new FailableResult(new VoidParams, false, msg)
    }
    
    project.delete
    return new FailableResult(new VoidParams)
  }
}