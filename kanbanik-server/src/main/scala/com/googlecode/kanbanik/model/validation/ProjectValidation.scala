package com.googlecode.kanbanik.model.validation
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.TaskScala

trait ProjectValidation {

  def canBeDeleted(project: ProjectScala): (Boolean, String) = {
    if (!project.tasks.isDefined) {
      return (true, "")
    }

    return composeResult(project.tasks.get)
  }
  
  def canBeRemoved(project: ProjectScala, board: BoardScala): (Boolean, String) = {
    if (!project.tasks.isDefined) {
      return (true, "")
    }

    val tasks = project.tasks.get.filter(_.workflowitem.board.id == board.id)
    if (tasks.size == 0) {
      return (true, "")
    }
    
    return composeResult(tasks)
  }
  
  def composeResult(tasks: List[TaskScala]): (Boolean, String) = {

    var msg = "There are some tasks associated with this project. Please delete them first and than try to do this action again. The tasks: [";
    for (task <- tasks) {
      msg += task.ticketId + " on board: " + task.workflowitem.board.name + ", ";
    }
    msg = msg.substring(0, msg.length() - 2);
    msg += "]";
    
    (false, msg)
  }
  
}