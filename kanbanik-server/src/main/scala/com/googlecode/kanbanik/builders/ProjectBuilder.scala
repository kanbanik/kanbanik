package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.TaskScala
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.dto.BoardDto

class ProjectBuilder {

  def buildDto(project: ProjectScala): ProjectDto = {
    val boardBuilder = new BoardBuilder

    val res = buildShallowDto(project)

    val boards = project.boards.getOrElse(List[BoardScala]())
    val tasks = project.tasks.getOrElse(List[TaskScala]())

    boards.foreach(board => res.addBoard(boardBuilder.buildDto(board)))
    tasks.foreach(task => {
      val taskDto = taskBuilder.buildDto(task)
      taskDto.setProject(res)
      res.addTask(taskDto)
    })

    res
  }

  def buildEntity(projectDto: ProjectDto): ProjectScala = {
    // only shallow for now
    new ProjectScala(
      {
        if (projectDto.getId() == null) {
          None
        } else {
          Some(new ObjectId(projectDto.getId()))
        }
      },
      projectDto.getName(),
      {
        dtosToEntities[BoardScala, BoardDto](projectDto.getBoards(), {board => BoardScala.byId(new ObjectId(board.getId()))})
      },

      {
        dtosToEntities[TaskScala, TaskDto](projectDto.getTasks(), {task => TaskScala.byId(new ObjectId(task.getId()))})
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

  def buildShallowDto(project: ProjectScala) = {
    val res = new ProjectDto
    res.setId(project.id.get.toString())
    res.setName(project.name)
    res
  }

  private[builders] def taskBuilder = new TaskBuilder
}