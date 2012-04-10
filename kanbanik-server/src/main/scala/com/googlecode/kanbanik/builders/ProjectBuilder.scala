package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.TaskScala

class ProjectBuilder {
  
  val boardBuilder = new BoardBuilder
  
  val taskBuilder = new TaskBuilder
  
  def buildDto(project: ProjectScala): ProjectDto = {
    val res = new ProjectDto
    res.setId(project.id.get.toString())
    res.setName(project.name)
    
    val boards = project.boards.getOrElse(List[BoardScala]())
    val tasks = project.tasks.getOrElse(List[TaskScala]())
    
    boards.foreach(board => res.addBoard(boardBuilder.buildDto(board)))
    tasks.foreach(task => res.addTask(taskBuilder.buildDto(task)))
    
    res
  }
}