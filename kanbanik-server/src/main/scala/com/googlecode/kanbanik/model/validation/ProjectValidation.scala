package com.googlecode.kanbanik.model.validation
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Task

trait ProjectValidation {

  def canBeDeleted(project: Project): (Boolean, String) = {
    val tasksOnProject = findTasksOnProject(project)
    if (tasksOnProject.isEmpty) {
      return (true, "")
    }

    composeResult(tasksOnProject)
  }

  def canBeRemoved(project: Project, board: Board): (Boolean, String) = {
    // especially here it is wasteful - but since there is rarely more than one board...
    val tasksOnProject = findTasksOnProject(project)
    if (tasksOnProject.isEmpty) {
      return (true, "")
    }

    val tasks = tasksOnProject.filter(_.boardId == board.id.get)
    if (tasks.size == 0) {
      return (true, "")
    }

    composeResult(tasks)
  }

  private def composeResult(tasks: List[Task]): (Boolean, String) = {
      val header = "There are some tasks associated with this project. Please delete them first and than try to do this action again. The tasks: [";
      val body = tasks.map(task => task.ticketId + " on board: " + task.board.name)
      val footer = "]"
      
      val msg = header + body.mkString(",") + footer

      (false, msg)
  }

  // REALLY heavy operation! It is based on assumption that there will be only few boards 
  // in the system - mostly one. As soon as this will not be true anymore, needs to be optimized!
  private def findTasksOnProject(project: Project) = for (board <- Board.all(includeTasks = true); task <- board.tasks; if task.projectId == project.id.get) yield task

}