package com.googlecode.kanbanik.builders
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.WorkflowitemScala

class WorkflowitemBuilder {
  def buildDto(workflowitem: WorkflowitemScala): WorkflowitemDto = {

    val res = new WorkflowitemDto
    res.setId(workflowitem.id.get.toString())
    res.setName(workflowitem.name)
    res.setWipLimit(workflowitem.wipLimit)

    val children = workflowitem.children.getOrElse(List[WorkflowitemScala]())
    children.foreach(child => res.addChild(buildDto(child)))

    res
  }
}