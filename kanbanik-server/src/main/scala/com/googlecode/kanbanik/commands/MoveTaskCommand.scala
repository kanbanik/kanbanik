package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.security._
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.{User, Workflowitem}
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos.{PermissionType, ErrorDto, TaskDto, MoveTaskDto}

class MoveTaskCommand extends Command[MoveTaskDto, TaskDto] with TaskManipulation with HasEntityLoader {

  private lazy val taskBuilder = new TaskBuilder()

  override def execute(params: MoveTaskDto, user: User): Either[TaskDto, ErrorDto] = {
    
    val oldTask = loadTask(new ObjectId(params.task.id.get), user).getOrElse(
        return Right(ErrorDto(ServerMessages.entityDeletedMessage("task")))
    )
    
    val newTask = taskBuilder.buildEntity(params.task)
    
    val somethingFailedResult = Right(ErrorDto("Some entity associated with this task does not exist any more - possibly has been deleted by a different user. Please refresh your browser to get the current data."))
    val realBoard = loadBoard(newTask.boardId, false).getOrElse(
      return somethingFailedResult
    )

    realBoard.workflow.findItem(Workflowitem(newTask.workflowitemId)).getOrElse(
      return somethingFailedResult
    )

    loadProject(new ObjectId(params.task.projectId), user).getOrElse(
    		return somethingFailedResult
    )

    val newOrder = calculateNewOrder(params)

    val toStore = oldTask.copy(order = newOrder, projectId = newTask.projectId, workflowitemId = newTask.workflowitemId)

    val resTask = if (newTask.boardId == oldTask.boardId) {
      toStore.store
    } else {
      // task was moved to a different board
      oldTask.delete(oldTask.boardId, user)

      toStore.copy(id = None, boardId = newTask.boardId).store
    }

    Left(taskBuilder.buildDto(resTask))
  }
  
  
  def calculateNewOrder(params: MoveTaskDto) = {
    val prevOrder = orderOrNull(params.prevOrder)
    val nextOrder = orderOrNull(params.nextOrder)

    if (prevOrder == null && nextOrder == null) {
      // the only one in the workflow
      "0"
    } else if (prevOrder == null || nextOrder == null) {
      if (prevOrder == null) {
        // to the first place
        val next = BigDecimal.apply(nextOrder)
        val newOrder = next - 100
        newOrder.toString
      } else {
        // to the last place
        val prev = BigDecimal.apply(prevOrder)
        val newOrder = prev + 100
        newOrder.toString
      }
    } else {
      // between two
      val prev = BigDecimal.apply(prevOrder)
      val next = BigDecimal.apply(nextOrder)

      if ((prev < 0 && next < 0) || (prev > 0 && next > 0)) {
        val newOrder = (next + prev) / 2
        newOrder.toString
      } else {
        val distance = next.abs + prev.abs
        val newOrder = prev + (distance / 2)
        newOrder.toString
      }
    }

  }

  private def orderOrNull(order: Option[String]) = {
    val orderWithEmpty = order.orNull
    if (orderWithEmpty == "") {
      null
    } else {
      orderWithEmpty
    }
  }

  override def checkPermissions(param: MoveTaskDto, user: User): Option[List[String]] = {

    val oldTask = loadTask(new ObjectId(param.task.id.get), user).getOrElse(
      // will be handled later on validations
      return None
    )

    doCheckPermissions(user, List(
      checkOneOf(PermissionType.MoveTask_b, param.task.boardId),
      checkOneOf(PermissionType.MoveTask_p, param.task.projectId),
      checkOneOf(PermissionType.MoveTask_b, oldTask.boardId.toString),
      checkOneOf(PermissionType.MoveTask_p, oldTask.projectId.toString)
    ))
  }

  override def filter(toReturn: TaskDto, user: User): Boolean =
    canRead(user, PermissionType.ReadBoard, toReturn.boardId) && canRead(user, PermissionType.ReadProject, toReturn.projectId)

}