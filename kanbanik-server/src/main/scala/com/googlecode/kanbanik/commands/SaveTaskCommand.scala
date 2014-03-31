package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dtos.{ErrorDto, TaskDto}

class SaveTaskCommand extends Command[TaskDto, TaskDto] with TaskManipulation {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(taskDto: TaskDto): Either[TaskDto, ErrorDto] = {
    if (taskDto.workflowitemId == null) {
      return Right(ErrorDto("At least one workflowitem must exist to create a task!"))
    }

    try {
      val board = getBoard(taskDto, false)
      val workdlowitemId = new ObjectId(taskDto.workflowitemId)
      board.workflow.findItem(Workflowitem().copy(id = Some(workdlowitemId))).getOrElse(throw new IllegalArgumentException())
    } catch {
      case e: IllegalArgumentException =>
        return Right(ErrorDto("The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data."))
    }

    val task = taskBuilder.buildEntity(taskDto)

    val stored = setOrderIfNeeded(taskDto, task).store
    return Left(taskBuilder.buildDto(stored))
  }
  
  def setOrderIfNeeded(taskDto: TaskDto, task: Task) = {
    if (task.id.isDefined) {
      task
    } else if (taskDto.order.isDefined) {
      task.copy(order = taskDto.order.get)
    } else {
      // only if not yet inserted and the order has not been sent, than look it up. But it is a REALLY heavy operation
      task.copy(order = findOrder(taskDto))
    }
  }

  def findOrder(task: TaskDto) = {
    val board = getBoard(task, true)
    val tasksOnWorkflowitem = board.tasks.filter(_.workflowitemId == task.workflowitemId)
    if (tasksOnWorkflowitem.size == 0) {
      "0"
    } else {
      val min = tasksOnWorkflowitem.foldLeft(tasksOnWorkflowitem.head)((x, y) => if (BigDecimal(x.order) <= BigDecimal(y.order)) x else y)
      val first = BigDecimal(min.order) - 100
      first.toString
    }

  }

  def getBoard(taskDto: TaskDto, includeTasks: Boolean) = {
    Board.byId(new ObjectId(taskDto.boardId), includeTasks)
  }

}