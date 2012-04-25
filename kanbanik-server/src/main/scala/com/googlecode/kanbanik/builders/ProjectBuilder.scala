package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.TaskScala

class ProjectBuilder {

  def buildDto(project: ProjectScala): ProjectDto = {
    val boardBuilder = new BoardBuilder

    val taskBuilder = new TaskBuilder
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

  def buildShallowDto(project: ProjectScala) = {
    val res = new ProjectDto
    res.setId(project.id.get.toString())
    res.setName(project.name)
    res
  }
}