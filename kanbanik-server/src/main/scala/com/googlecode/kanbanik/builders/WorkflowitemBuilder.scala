package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId

import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.WorkflowitemScala


class WorkflowitemBuilder {

  lazy val boardBuilder = new BoardBuilder()
  
  def buildEntity(dto: WorkflowitemDto): WorkflowitemScala = {
    new WorkflowitemScala(
        findId(dto),
        dto.getName(),
        dto.getWipLimit(),
        dto.getItemType().asStringValue(),
        findWorkflowitem(dto.getChild()),
        findWorkflowitem(dto.getNextItem()),
        BoardScala.byId(new ObjectId(dto.getBoard().getId()))
    )
  }
  
  def findWorkflowitem(dto: WorkflowitemDto): Option[WorkflowitemScala] = {
    if (dto == null || dto.getId() == null) {
      return None
    }
    
    return Some(WorkflowitemScala.byId(new ObjectId(dto.getId())))
  }
  
  def findId(dto: WorkflowitemDto): Option[ObjectId] = {
    if (dto.getId() == null) {
      return None
    } 

    Some(new ObjectId(dto.getId()))
  }
  
  def buildDto(workflowitem: WorkflowitemScala): WorkflowitemDto = {

    val res = buildDtoNonRecursive(workflowitem)

    if (workflowitem.child.isDefined) {
      res.setChild(buildDto(workflowitem.child.get))
    }

    if (workflowitem.nextItem.isDefined) {
      res.setNextItem(buildDto(workflowitem.nextItem.get))
    }

    res
  }

  def buildDtoNonRecursive(workflowitem: WorkflowitemScala) = {
    val dto = new WorkflowitemDto
    dto.setId(workflowitem.id.get.toString())
    dto.setName(workflowitem.name)
    dto.setWipLimit(workflowitem.wipLimit)
    dto.setItemType(ItemType.asItemType(workflowitem.itemType))
    dto.setBoard(boardBuilder.buildShallowDto(workflowitem.board))
    dto
  }
}
  
