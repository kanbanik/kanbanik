package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.dto.TaskDto
import scala.collection.mutable.ListBuffer
import java.util.ArrayList

class BoardBuilder extends BaseBuilder {

  val workflowBuilder = new WorkflowBuilder

  val taskBuilder = new TaskBuilder
  
  def buildDto(board: Board): BoardDto = {
    buildDto(board, None)
  }

  def buildShallowDto(board: Board, workflow: Option[WorkflowDto]): BoardDto = {
    val res = new BoardDto
    res.setName(board.name)
    res.setBalanceWorkflowitems(board.balanceWorkflowitems)
    res.setId(board.id.get.toString())
    res.setVersion(board.version)
    res
  }
  
  def buildDto(board: Board, workflow: Option[WorkflowDto]): BoardDto = {
    val res = buildShallowDto(board, workflow)
    res.setWorkflow(workflow.getOrElse(workflowBuilder.buildDto(board.workflow, Some(res))))
    val cache = new WorkflowitemCache(List(res))
    val tasks = board.tasks.map(taskBuilder.buildDto(_, Some(cache)))
    val javaList = new ArrayList[TaskDto]
    tasks.foreach (javaList.add (_))  
    res.setTasks(javaList)
    res
  }

  def buildEntity(boardDto: BoardDto): Board = {
    val board = new Board(
      determineId(boardDto),
      boardDto.getName(),
      boardDto.isBalanceWorkflowitems(),
      boardDto.getVersion(),
      Workflow(),
      boardDto.getTasks().toArray.toList.map(task => taskBuilder.buildEntity(task.asInstanceOf[TaskDto]))
    )
    
    board.withWorkflow(workflowBuilder.buildEntity(boardDto.getWorkflow(), Some(board)))
      
  }

  private[builders] def workflowitemBuilder = new WorkflowitemBuilder
}