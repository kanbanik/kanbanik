package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Project
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board

class SaveTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[SimpleParams[TaskDto]]] with TaskManipulation {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: SimpleParams[TaskDto]): FailableResult[SimpleParams[TaskDto]] = {
    val taskDto = params.getPayload
    if (taskDto.getWorkflowitem() == null) {
      return new FailableResult(null, false, "At least one workflowitem must exist to create a task!")
    }

    try {
      val board = getBoard(taskDto, false)
      val workdlowitemId = new ObjectId(taskDto.getWorkflowitem().getId())
      board.workflow.findItem(Workflowitem().copy(id = Some(workdlowitemId))).getOrElse(throw new IllegalArgumentException())
    } catch {
      case e: IllegalArgumentException =>
        return new FailableResult(new SimpleParams(taskDto), false, "The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data.")
    }

    val task = taskBuilder.buildEntity(taskDto)
    
    try {
      val stored = setOrderIfNeeded(taskDto, task).store
      return new FailableResult(new SimpleParams(taskBuilder.buildDto(stored, None)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(taskDto), false, ServerMessages.midAirCollisionException)
    }

  }
  
  def setOrderIfNeeded(taskDto: TaskDto, task: Task) = {
    if (task.id.isDefined) {
      task
    } else if (taskDto.getOrder != null) {
      task.copy(order = taskDto.getOrder)
    } else {
      // only if not yet inserted and the order has not been sent, than look it up. But it is a REALLY heavy operation
      task.copy(order = findOrder(taskDto))
    }
  }

  def findOrder(task: TaskDto) = {
    val board = getBoard(task, true)
    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitem == Workflowitem().copy(id = Some(new ObjectId(task.getWorkflowitem().getId()))))
    if (tasksOnWorkflowitem.size == 0) {
      "0"
    } else {
      val min = tasksOnWorkflowitem.foldLeft(tasksOnWorkflowitem.head)((x, y) => if (BigDecimal(x.order) <= BigDecimal(y.order)) x else y)
      val first = BigDecimal(min.order) - 100
      first.toString
    }

  }

  def getBoard(taskDto: TaskDto, includeTasks: Boolean) = {
    val boardId = taskDto.getWorkflowitem().getParentWorkflow().getBoard().getId()
    Board.byId(new ObjectId(boardId), includeTasks)
  }

}