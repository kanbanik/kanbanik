package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.builders.ProjectBuilder

class GetAllProjectsCommand extends ServerCommand[VoidParams, SimpleParams[ListDto[ProjectDto]]] {

  lazy val projectBuilder = new ProjectBuilder()
  
  def execute(params: VoidParams): SimpleParams[ListDto[ProjectDto]] = {
    val allProjects = new ListDto[ProjectDto]

    for (project <- ProjectScala.all()) {
      allProjects.addItem(projectBuilder.buildDto(project))
    }
    
    new SimpleParams(allProjects)
  }
  
}