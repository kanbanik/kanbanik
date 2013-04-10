package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ClassOfService
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.commands.TaskManipulation
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.dto.BoardDto
import scala.collection.mutable.HashMap
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.WorkflowDto

class TaskBuilder extends TaskManipulation {

  def buildDto(task: Task, cache: Option[WorkflowitemCache]): TaskDto = {
    val dto = new TaskDto
    dto.setId(task.id.get.toString())
    dto.setName(task.name)
    dto.setDescription(task.description)
    dto.setClassOfService(ClassOfService.fromId(task.classOfService))

    if (cache.isDefined) {
      dto.setWorkflowitem(cache.get.getWorkflowitem(task.workflowitem.id.get).getOrElse(workflowitemBuilder.buildShallowDto(task.workflowitem, None)))
    } else {
      dto.setWorkflowitem(workflowitemBuilder.buildShallowDto(task.workflowitem, None))
    }

    dto.setTicketId(task.ticketId)
    dto.setVersion(task.version)
    dto.setOrder(task.order)

    dto.setProject(projectBuilder.buildShallowDto(task.project))
    dto
  }

  def buildEntity(taskDto: TaskDto): Task = {
    new Task(
      determineId(taskDto),
      taskDto.getName(),
      taskDto.getDescription(),
      taskDto.getClassOfService().getId(),
      determineTicketId(taskDto),
      taskDto.getVersion(),
      taskDto.getOrder(),
      workflowitemBuilder.buildShallowEntity(taskDto.getWorkflowitem(), None, None),
      projectBuilder.buildShallowEntity(taskDto.getProject()))
  }

  private def determineId(taskDto: TaskDto): Option[ObjectId] = {
    if (taskDto.getId() != null) {
      return Some(new ObjectId(taskDto.getId()))
    }

    None

  }

  private def determineTicketId(taskDto: TaskDto): String = {
    if (taskDto.getId() == null) {
      return generateUniqueTicketId()
    }

    if (taskDto.getId() != null && taskDto.getTicketId() == null) {
      throw new IllegalStateException("The task " + taskDto.getId() + " has not set a ticket id!")
    }

    taskDto.getTicketId()
  }

  private[builders] def workflowitemBuilder = new WorkflowitemBuilder

  private[builders] def projectBuilder = new ProjectBuilder
}

class WorkflowitemCache(val boards: List[BoardDto]) {

  val alreadyFoundItems = HashMap[ObjectId, WorkflowitemDto]()

  def getWorkflowitem(id: ObjectId): Option[WorkflowitemDto] = {
    val res = alreadyFoundItems.get(id)
    if (res.isDefined) {
      res
    } else {
      findItem(id)
    }

  }

  private def findItem(id: ObjectId): Option[WorkflowitemDto] = {

    def traverseBoards(boardsToTraverse: List[BoardDto]): Option[WorkflowitemDto] = {
      boardsToTraverse match {
        case Nil => None
        case first :: rest => {
          val res = findOnBoard(id, first)
          if (res.isDefined) {
            res
          } else {
            traverseBoards(rest)
          }
        }
      }
    }

    val found = traverseBoards(boards)

    if (found.isDefined) {
      alreadyFoundItems.put(id, found.get)
    }

    found
  }

  private def findOnBoard(id: ObjectId, board: BoardDto): Option[WorkflowitemDto] = {

    def traverseWorkflowitems(workflowitems: List[WorkflowitemDto]): Option[WorkflowitemDto] = {
      workflowitems match {
        case Nil => None
        case first :: rest => {
          if (new ObjectId(first.getId()) == id) {
            return Some(copyToShallowWorkflowitem(first))
          }

          val nested = traverseWorkflowitems(asList(first.getNestedWorkflow().getWorkflowitems()))
          if (nested.isDefined) {
            nested
          } else {
            traverseWorkflowitems(rest)
          }
        }
      }
    }

    def copyToShallowWorkflowitem(from: WorkflowitemDto): WorkflowitemDto = {
      val res = new WorkflowitemDto()
      res.setId(from.getId)
      res.setName(from.getName)
      res.setVersion(from.getVersion)
      res.setItemType(from.getItemType())
      res.setWipLimit(from.getWipLimit())

      val parentBoard = new BoardDto
      parentBoard.setName(from.getParentWorkflow().getBoard().getName)
      parentBoard.setId(from.getParentWorkflow().getBoard().getId)
      parentBoard.setVersion(from.getParentWorkflow().getBoard().getVersion)

      val parentWorkflow = new WorkflowDto
      parentWorkflow.setId(from.getParentWorkflow().getId)
      parentWorkflow.setBoard(parentBoard)
      res.setParentWorkflow(parentWorkflow)

      res
    }
    traverseWorkflowitems(asList(board.getWorkflow().getWorkflowitems()))
  }

  def asList(items: java.util.List[WorkflowitemDto]) = items.toArray().toList.asInstanceOf[List[WorkflowitemDto]]
}