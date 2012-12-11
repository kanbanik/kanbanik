package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.dto.BoardDto

class ProjectBuilder {

  def buildDto(project: Project): ProjectDto = {
    val boardBuilder = new BoardBuilder

    val res = buildShallowDto(project)

    val boards = project.boards.getOrElse(List[Board]())
    val tasks = project.tasks.getOrElse(List[Task]())

    boards.foreach(board => res.addBoard(boardBuilder.buildDto(board, None)))
    tasks.foreach(task => {
      val taskDto = taskBuilder.buildDto(task)
      taskDto.setProject(res)
      res.addTask(taskDto)
    })

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
        dtosToEntities[Board, BoardDto](projectDto.getBoards(), {board => Board.byId(new ObjectId(board.getId()))})
      },

      {
        dtosToEntities[Task, TaskDto](projectDto.getTasks(), {task => Task.byId(new ObjectId(task.getId()))})
      })
  }

  def dtosToEntities[E, D](dtos: java.util.List[D], f: D => E): Option[List[E]] = {
    var entities = List[E]()
    for (i <- 0 until dtos.size()) {
      entities = f(dtos.get(i)) :: entities
    }

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