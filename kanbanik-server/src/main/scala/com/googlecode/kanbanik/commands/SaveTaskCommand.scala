package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.{User, Task, Workflowitem, Board}
import com.googlecode.kanbanik.security._
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dtos.{PermissionType, ErrorDto, TaskDto}

class SaveTaskCommand extends Command[TaskDto, TaskDto] with TaskManipulation {

  private lazy val taskBuilder = new TaskBuilder()

  override def execute(taskDto: TaskDto): Either[TaskDto, ErrorDto] = {
    if (taskDto.workflowitemId == null) {
      return Right(ErrorDto("At least one workflowitem must exist to create a task!"))
    }

    try {
      val board = getBoard(taskDto, includeTasks = false)
      val workdlowitemId = new ObjectId(taskDto.workflowitemId)
      board.workflow.findItem(Workflowitem().copy(id = Some(workdlowitemId))).getOrElse(throw new IllegalArgumentException())
    } catch {
      case e: IllegalArgumentException =>
        return Right(ErrorDto("The worflowitem on which this task is defined does not exist. Possibly it has been deleted by a different user. Please refresh your browser to get the current data."))
    }

    val task = taskBuilder.buildEntity(taskDto)

    val stored = setOrderIfNeeded(taskDto, task).store()
    Left(taskBuilder.buildDto(stored))
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
    val board = getBoard(task, includeTasks = true)
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

  override def checkPermissions(param: TaskDto, user: User): Option[List[String]] = {
    val checks = if (param.id.isDefined) {

      val oldTask = try {
        Task.byId(new ObjectId(param.id.get), user)
      } catch {
        // well... someone have deleted it in meanwhile so no need to do this checks
        case _ => return None
      }

      val readUserCheck = if (param.assignee.isDefined) {
        if (!oldTask.assignee.isDefined || oldTask.assignee.get.name != param.assignee.get.userName) {
          checkOneOf(PermissionType.ReadUser, param.assignee.get.userName)
        } else {
          alwaysPassingCheck()
        }
      } else {
        alwaysPassingCheck()
      }

      val readClassOfServiceCheck = if (param.classOfService.isDefined) {
        if (!oldTask.classOfService.isDefined || oldTask.classOfService.get.id.get != param.classOfService.get.id.get) {
          checkOneOf(PermissionType.ReadClassOfService, param.classOfService.get.id.get.toString)
        } else {
          alwaysPassingCheck()
        }
      } else {
        alwaysPassingCheck()
      }

      readClassOfServiceCheck :: readUserCheck :: List(
        checkOneOf(PermissionType.EditTask_b, param.boardId),
        checkOneOf(PermissionType.EditTask_p, param.projectId)
      )
    } else {
      List(
        checkOneOf(PermissionType.CreateTask_b, param.boardId),
        checkOneOf(PermissionType.CreateTask_p, param.projectId)
      )
    }

    doCheckPermissions(user, checks)
  }

  override def filter(toReturn: TaskDto, user: User): Boolean =
    canRead(user, PermissionType.ReadBoard, toReturn.boardId) && canRead(user, PermissionType.ReadProject, toReturn.projectId)
}