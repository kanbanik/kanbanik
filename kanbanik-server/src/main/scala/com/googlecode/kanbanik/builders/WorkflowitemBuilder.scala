package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.model.Workflow


class WorkflowitemBuilder extends BaseBuilder {
  
  def buildShallowEntity(dto: WorkflowitemDto, parentWorkflow: Option[Workflow], board: Option[Board]): Workflowitem = {
     new Workflowitem(
        {
          if (dto.getId() == null) {
            Some(new ObjectId)
          } else {
            Some(new ObjectId(dto.getId()))
          }
        },
        dto.getName(),
        dto.getWipLimit(),
        dto.getItemType().asStringValue(),
        dto.getVersion(),
        // don't calculate it if not needed
        Workflow(), 
        parentWorkflow
    )
  }
  
  def buildEntity(dto: WorkflowitemDto, parentWorkflow: Option[Workflow], board: Option[Board]): Workflowitem = {
    new Workflowitem(
        {
          if (dto.getId() == null) {
            Some(new ObjectId)
          } else {
            Some(new ObjectId(dto.getId()))
          }
        },
        dto.getName(),
        dto.getWipLimit(),
        dto.getItemType().asStringValue(),
        dto.getVersion(),
        workflowBuilder.buildEntity(dto.getNestedWorkflow(), board), 
        parentWorkflow
    )
  }
  
   def buildShallowDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto]): WorkflowitemDto = {
    val dto = new WorkflowitemDto
    dto.setId(workflowitem.id.get.toString())
    dto.setName(workflowitem.name)
    dto.setWipLimit(workflowitem.wipLimit)
    dto.setItemType(ItemType.asItemType(workflowitem.itemType))
    dto.setVersion(workflowitem.version)
    dto.setParentWorkflow(parentWorkflow.getOrElse(workflowBuilder.buildShallowDto(workflowitem.parentWorkflow, parentBoard(parentWorkflow))))
    dto
  }
  
  def buildDto(workflowitem: Workflowitem, parentWorkflow: Option[WorkflowDto]): WorkflowitemDto = {
    val dto = buildShallowDto(workflowitem, parentWorkflow)
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
  
