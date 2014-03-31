package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.dtos.ListDto
import com.googlecode.kanbanik.dtos.ProjectDto
import com.googlecode.kanbanik.dtos.EmptyDto

class GetAllProjectsCommand extends Command[EmptyDto, ListDto[ProjectDto]] {

  lazy val projectBuilder = new ProjectBuilder()

  def execute(params: EmptyDto): Either[ListDto[ProjectDto], ErrorDto] = {

    val dtos = Project.all().map(projectBuilder.buildDto(_))

    Left(ListDto(dtos))
  }
  
}