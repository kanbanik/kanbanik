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

    return composeResult(tasksOnProject)
  }
  
  def canBeRemoved(project: Project, board: Board): (Boolean, String) = {
    val tasksOnProject = findTasksOnProject(project)
    if (tasksOnProject.isEmpty) {
      return (true, "")
    }

    val tasks = tasksOnProject.filter(_.workflowitem.parentWorkflow.board.id == board.id)
    if (tasks.size == 0) {
      return (true, "")
    }
    
    return composeResult(tasks)
  }
  
  def composeResult(tasks: List[Task]): (Boolean, String) = {

    var msg = "There are some tasks associated with this project. Please delete them first and than try to do this action again. The tasks: [";
    for (task <- tasks) {
      msg += task.ticketId + " on board: " + task.workflowitem.parentWorkflow.board.name + ", ";
    }
    msg = msg.substring(0, msg.length() - 2);
    msg += "]";
    
    (false, msg)
  }
  
  // REALLY heavy operation! It is based on assumption that there will be only few boards 
  // in the system - mostly one. As soon as this will not be true anymore, needs to be optimized!
  private def findTasksOnProject(project: Project) = for (board <- Board.all; task <- board.tasks; if(task.project.equals(project))) yield task 
  
}