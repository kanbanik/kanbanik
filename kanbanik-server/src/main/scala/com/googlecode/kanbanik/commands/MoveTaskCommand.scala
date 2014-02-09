package com.googlecode.kanbanik.commands

import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos.{ErrorDto, TaskDto, MoveTaskDto}

class MoveTaskCommand extends Command[MoveTaskDto, TaskDto] with TaskManipulation with HasEntityLoader {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: MoveTaskDto): Either[TaskDto, ErrorDto] = {
    
    val oldTask = loadTask(new ObjectId(params.task.id.get)).getOrElse(
        return Right(ErrorDto(ServerMessages.entityDeletedMessage("task")))
    )
    
    val newTask = taskBuilder.buildEntity2(params.task)
    
    val somethingFaildResult = Right(ErrorDto("Some entity associated with this task does not exist any more - possibly has been deleted by a different user. Please refresh your browser to get the currrent data."))
    val realBoard = loadBoard(newTask.boardId, false).getOrElse(
      return somethingFaildResult
    )

    realBoard.workflow.findItem(Workflowitem(newTask.workflowitemId)).getOrElse(
      return somethingFaildResult
    )

    loadProject(new ObjectId(params.task.projectId)).getOrElse(
    		return somethingFaildResult
    )

    val newOrder = calculateNewOrder(params)
    val toStore = oldTask.copy(order = newOrder, projectId = newTask.projectId, workflowitemId = newTask.workflowitemId)
    val resTask = toStore.store
    Left(taskBuilder.buildDto2(resTask))

  }
  
  
  def calculateNewOrder(params: MoveTaskDto) = {
    val prevOrder = params.prevOrder.getOrElse(null)
    val nextOrder = params.nextOrder.getOrElse(null)

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
}