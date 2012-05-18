package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.builders.WorkflowitemBuilder
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId
import scala.util.control.Breaks.break
import scala.util.control.Breaks.breakable
import com.googlecode.kanbanik.model.KanbanikEntity
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.dto.WorkflowitemDto

class EditWorkflowCommand extends ServerCommand[EditWorkflowParams, VoidParams] with KanbanikEntity {

  lazy val workflowitemBuilder = new WorkflowitemBuilder

  def execute(params: EditWorkflowParams): VoidParams = {

    val parentDto = params.getParent()
    val currenDto = params.getCurrent()
    val nextOfCurrentDto = params.getCurrent().getNextItem()

    if (currenDto.getId() != null) {
      val prevCurrent = WorkflowitemScala.byId(new ObjectId(currenDto.getId()))
      val prevParent = findParent(prevCurrent)
      if (prevParent.isDefined) {
        prevParent.get.child = prevCurrent.nextItem
        prevParent.get.store
      }

    }

    val currentEntity = workflowitemBuilder.buildEntity(currenDto)

    currentEntity.store

    if (parentDto != null) {
      val parentEntity = workflowitemBuilder.buildEntity(parentDto)
      parentEntity.child = Some(currentEntity)
      parentEntity.store
    }

    updateBoard(currenDto, parentDto, currentEntity)

    new VoidParams
  }

  /**
   * The board.workflowitems can contain only workflowitems which has no parent (e.g. top level entities)
   * This method ensures it.
   */
  private def updateBoard(currentDto: WorkflowitemDto, parentDto: WorkflowitemDto, currentEntity: WorkflowitemScala) {
    val board = BoardScala.byId(new ObjectId(currentDto.getBoard().getId()))
    val isInBoard = findIfIsInBoard(board, currentEntity)
    val hasNewParent = parentDto != null

    if (isInBoard && hasNewParent) {
      if (board.workflowitems.isDefined) {
        board.workflowitems = Some(board.workflowitems.get.filter(_.id != currentEntity.id))
        board.store
      }
    } else if (!isInBoard && !hasNewParent) {
      if (board.workflowitems.isDefined) {
        board.workflowitems = Some(currentEntity :: board.workflowitems.get)
        board.store
      } else {
        board.workflowitems = Some(List(currentEntity))
        board.store
      }
    }
  }

  private def findIfIsInBoard(board: BoardScala, workflowitem: WorkflowitemScala): Boolean = {

    if (!board.workflowitems.isDefined) {
      return false
    }

    board.workflowitems.get.foreach(item => {
      if (item.id.get == workflowitem.id.get) {
        return true
      }
    })

    false
  }

  def findParent(child: WorkflowitemScala): Option[WorkflowitemScala] = {
    using(createConnection) { conn =>
      val maxTaskId = coll(conn, Coll.Workflowitems).find()
      val parent = coll(conn, Coll.Workflowitems).findOne(MongoDBObject("childId" -> child.id))

      if (parent.isDefined) {
        return Some(WorkflowitemScala.byId(parent.get.get("_id").asInstanceOf[ObjectId]));
      } else {
        return None
      }

    }
  }

}