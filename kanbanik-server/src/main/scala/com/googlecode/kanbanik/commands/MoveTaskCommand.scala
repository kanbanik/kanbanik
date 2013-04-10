package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.shell.MoveTaskParams
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.db.HasEntityLoader

class MoveTaskCommand extends ServerCommand[MoveTaskParams, FailableResult[SimpleParams[TaskDto]]] with TaskManipulation with HasEntityLoader {

  private lazy val taskBuilder = new TaskBuilder()

  def execute(params: MoveTaskParams): FailableResult[SimpleParams[TaskDto]] = {
	try {
	  Task.byId(new ObjectId(params.getTask().getId()))
	} catch {
		case e: IllegalArgumentException =>
	  		return new FailableResult(new SimpleParams(params.getTask()), false, ServerMessages.entityDeletedMessage("task"))
	}
	println("1")  
    val task = taskBuilder.buildEntity(params.getTask())
    
    val somethingFaildResult = new FailableResult(new SimpleParams(params.getTask()), false, "Some entity associated with this task does not exist any more - possibly has been deleted by a different user. Please refresh your browser to get the currrent data.")
    println("2")
    val realBoard = loadBoard(task.workflowitem.parentWorkflow.board.id.get, false).getOrElse(
        return somethingFaildResult
    )
    println("3")
    realBoard.workflow.findItem(task.workflowitem).getOrElse(
    			return new FailableResult(new SimpleParams(params.getTask()), false, "The workflowitem on which this task existed does not exist any more - possibly has been deleted by a different user")    
    		)
    println("4")
    val project = loadProject(new ObjectId(params.getProject().getId())).getOrElse(
    		return somethingFaildResult
    )
    println("5")
    val newOrder = calculateNewOrder(params)
    println("6: " + newOrder)
    try {
    	val resTask = task.withOrder(newOrder).store
	    return new FailableResult(new SimpleParams(taskBuilder.buildDto(resTask, None)))
    } catch {
      case e: MidAirCollisionException =>
        return new FailableResult(new SimpleParams(taskBuilder.buildDto(task, None)), false, ServerMessages.midAirCollisionException)
    }
    
  }
  
  
  def calculateNewOrder(params: MoveTaskParams) = {
    if (params.getPrevOrder() == null && params.getNextOrder() == null) {
      println("a")
      // the only one in the workflow
      "0"
    } else if (params.getPrevOrder() == null || params.getNextOrder() == null) {
      println("b")
      if (params.getPrevOrder() == null) {
        println("c")
        // to the first place
        val next = BigDecimal.apply(params.getNextOrder())
        val newOrder = next - 100
        newOrder.toString
      } else {
        println("d")
        // to the last place
        val prev = BigDecimal.apply(params.getPrevOrder())
        val newOrder = prev + 100
        newOrder.toString
      }
    } else {
      println("e")
      // between two
      val prev = BigDecimal.apply(params.getPrevOrder())
      val next = BigDecimal.apply(params.getNextOrder())

      if ((prev < 0 && next < 0) || (prev > 0 && next > 0)) {
        println("f")
        val newOrder = (next + prev) / 2
        newOrder.toString
      } else {
        println("g")
        val distance = next.abs + prev.abs
        val newOrder = prev + (distance / 2)
        newOrder.toString
      }
    }

  }
}