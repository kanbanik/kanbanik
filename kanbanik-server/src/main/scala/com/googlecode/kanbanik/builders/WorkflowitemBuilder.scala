package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId

import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem


class WorkflowitemBuilder {

  def buildEntity(dto: WorkflowitemDto): Workflowitem = {
    new Workflowitem(
        findId(dto),
        dto.getName(),
        dto.getWipLimit(),
        dto.getItemType().asStringValue(),
        findWorkflowitem(dto.getChild()),
        findWorkflowitem(dto.getNextItem()),
        Board.byId(new ObjectId(dto.getBoard().getId()))
    )
  }
  
  def findWorkflowitem(dto: WorkflowitemDto): Option[Workflowitem] = {
    if (dto == null || dto.getId() == null) {
      return None
    }
    
    return Some(Workflowitem.byId(new ObjectId(dto.getId())))
  }
  
  def findId(dto: WorkflowitemDto): Option[ObjectId] = {
    if (dto.getId() == null) {
      return None
    } 

    Some(new ObjectId(dto.getId()))
  }
  
  def buildDto(workflowitem: Workflowitem): WorkflowitemDto = {

    val res = buildDtoNonRecursive(workflowitem)

    if (workflowitem.child.isDefined) {
      res.setChild(buildDto(workflowitem.child.get))
    }

    if (workflowitem.nextItem.isDefined) {
      res.setNextItem(buildDto(workflowitem.nextItem.get))
    }

    res
  }

  def buildDtoNonRecursive(workflowitem: Workflowitem) = {
    val dto = new WorkflowitemDto
    dto.setId(workflowitem.id.get.toString())
    dto.setName(workflowitem.name)
    dto.setWipLimit(workflowitem.wipLimit)
    dto.setItemType(ItemType.asItemType(workflowitem.itemType))
    dto.setBoard(boardBuilder.buildShallowDto(workflowitem.board))
    dto
  }
  
  private[builders] def boardBuilder = new BoardBuilder
}
  
