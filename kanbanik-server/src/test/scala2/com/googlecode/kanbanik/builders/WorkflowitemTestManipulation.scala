package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.dto.ItemType
import org.bson.types.ObjectId

trait WorkflowitemTestManipulation {
 
  def itemDtoWithName(name: String): WorkflowitemDto = {
    val item = new WorkflowitemDto
    item.setName(name)
    item.setItemType(ItemType.HORIZONTAL)
    item.setId(new ObjectId().toString())
    item.setNestedWorkflow(new WorkflowDto())
    item.setParentWorkflow(new WorkflowDto())
    item

  }
}