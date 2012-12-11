package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.dto.WorkflowDto


class WorkflowitemBuilder {

  
  def buildEntity(dto: WorkflowitemDto): Workflowitem = {
    //    TODO-ref
//    new Workflowitem(
//        findId(dto),
//        dto.getName(),
//        dto.getWipLimit(),
//        dto.getItemType().asStringValue(),
//        dto.getVersion(),
//        findWorkflowitem(dto.getChild()),
//        findWorkflowitem(dto.getNextItem()),
//        Board.byId(new ObjectId(dto.getBoard().getId()))
//    )
    null
  }
  
  def findWorkflowitem(dto: WorkflowitemDto): Option[Workflowitem] = {
    if (dto == null || dto.getId() == null) {
      return None
    }
    null
//    return Some(Workflowitem.byId(new ObjectId(dto.getId())))
  }
  
  def findId(dto: WorkflowitemDto): Option[ObjectId] = {
    if (dto.getId() == null) {
      return None
    } 

    Some(new ObjectId(dto.getId()))
  }

  def buildDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto]): WorkflowitemDto = {
    val dto = new WorkflowitemDto
    dto.setId(workflowitem.id.get.toString())
    dto.setName(workflowitem.name)
    dto.setWipLimit(workflowitem.wipLimit)
    dto.setItemType(ItemType.asItemType(workflowitem.itemType))
    dto.setVersion(workflowitem.version)
    dto.setNestedWorkflow(workflowBuilder.buildDto(workflowitem.nestedWorkflow, parentBoard(parentWorkflow)))
    dto.setParentWorkflow(parentWorkflow.getOrElse(workflowBuilder.buildDto(workflowitem.parentWorkflow, parentBoard(parentWorkflow))))
    dto
  }
  
  def parentBoard(parentWorkflow: Option[WorkflowDto]) = {
    if (!parentWorkflow.isDefined) {
      None
    } else {
      Some(parentWorkflow.get.getBoard())
    }
  }
  
  def workflowBuilder = new WorkflowBuilder
}
  
