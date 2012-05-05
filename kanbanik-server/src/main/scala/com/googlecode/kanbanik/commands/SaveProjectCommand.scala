package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.builders.ProjectBuilder

class SaveProjectCommand extends ServerCommand[SimpleParams[ProjectDto], SimpleParams[ProjectDto]] {

  private lazy val projectBuilder = new ProjectBuilder()
  
  def execute(params: SimpleParams[ProjectDto]): SimpleParams[ProjectDto] = {
    val project = projectBuilder.buildEntity(params.getPayload())
    new SimpleParams(projectBuilder.buildDto(project.store))
  }
}