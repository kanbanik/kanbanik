package com.googlecode.kanbanik.model.validation
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Task

trait ProjectValidation {

  def canBeDeleted(project: Project): (Boolean, String) = {
    if (!project.tasks.isDefined) {
      return (true, "")
    }

    return composeResult(project.tasks.get)
  }
  
  def canBeRemoved(project: Project, board: Board): (Boolean, String) = {
    if (!project.tasks.isDefined) {
      return (true, "")
    }

    val tasks = project.tasks.get.filter(_.workflowitem.parentWorkflow.board.id == board.id)
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
  
}