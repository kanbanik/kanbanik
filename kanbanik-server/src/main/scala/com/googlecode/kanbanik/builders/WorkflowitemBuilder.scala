package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.WorkflowitemScala
import com.googlecode.kanbanik.dto.ItemType

class WorkflowitemBuilder {

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
    dto
  }
}
  
