package com.googlecode.kanbanik.builders

import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.dtos.{ProjectDto => NewProjectDto}

class ProjectBuilder {

  def buildDto2(project: Project) = {
    NewProjectDto(
      Some(project.id.get.toString),
      Some(project.name),
      if (!project.boards.isEmpty) {
        Some(project.boards.get.map(_.id.get.toString))
      } else {
        None
      },
      project.version
    )
  }

  def buildEntity2(projectDto: NewProjectDto): Project = {
    new Project(
    {
      if (!projectDto.id.isDefined) {
        None
      } else {
        Some(new ObjectId(projectDto.id.get))
      }
    },
    projectDto.name.getOrElse(""),
    projectDto.version,
    {
      if(projectDto.boardIds.isDefined) {
        Some(projectDto.boardIds.get.map(boardId => Board().copy(id = Some(new ObjectId(boardId)))))
      } else {
        None
      }
    }
    )
  }

  def dtosToEntities[E, D](dtos: java.util.List[D], f: D => E): Option[List[E]] = {
    val entities = dtos.toScalaList.map(f(_))

    if (entities.length == 0) {
      None
    } else {
      Some(entities)
    }
  }

}