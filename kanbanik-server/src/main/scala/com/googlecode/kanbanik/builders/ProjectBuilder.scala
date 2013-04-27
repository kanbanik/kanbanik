package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.commons._

class ProjectBuilder {

  def buildDto(project: Project): ProjectDto = {
    val boardBuilder = new BoardBuilder

    val res = buildShallowDto(project)

    val boards = project.boards.getOrElse(List[Board]())

    boards.foreach(board => res.addBoard(boardBuilder.buildDto(board, None)))

    res
  }

  def buildEntity(projectDto: ProjectDto): Project = {
    new Project(
      {
        if (projectDto.getId() == null) {
          None
        } else {
          Some(new ObjectId(projectDto.getId()))
        }
      },
      projectDto.getName(),
      projectDto.getVersion(),
      {
        dtosToEntities[Board, BoardDto](projectDto.getBoards(), {board => Board.byId(new ObjectId(board.getId()), false)})
      }
     )
  }
  
  def buildShallowEntity(projectDto: ProjectDto): Project = {
    new Project(
      {
        if (projectDto.getId() == null) {
          None
        } else {
          Some(new ObjectId(projectDto.getId()))
        }
      },
      projectDto.getName(),
      projectDto.getVersion(),
      null
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

  def buildShallowDto(project: Project) = {
    val res = new ProjectDto
    res.setId(project.id.get.toString())
    res.setName(project.name)
    res.setVersion(project.version)
    res
  }

  private[builders] def taskBuilder = new TaskBuilder
}