package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.MidAirCollisionException

class SaveProjectCommand extends ServerCommand[SimpleParams[ProjectDto], FailableResult[SimpleParams[ProjectDto]]] {

  private lazy val projectBuilder = new ProjectBuilder()
  
  def execute(params: SimpleParams[ProjectDto]): FailableResult[SimpleParams[ProjectDto]] = {
    val project = projectBuilder.buildEntity(params.getPayload())
    var res: ProjectDto = null
    try {
    	res = projectBuilder.buildDto(project.store)
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(res), false, ServerMessages.midAirCollisionException)
    }
    new FailableResult(new SimpleParams(res))
  }
}