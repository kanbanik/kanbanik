package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dtos.{WorkflowitemDto, WorkflowDto, WorkflowitemType}
import org.bson.types.ObjectId

trait WorkflowitemTestManipulation {

  def itemDtoWithName(name: String): WorkflowitemDto = {
    itemDtoWithName(name, None)
  }

  def itemDtoWithName(name: String, parentWorkflow: Option[WorkflowDto]): WorkflowitemDto = {
    new WorkflowitemDto(
      name,
      Some(new ObjectId().toString()),
      None,
      WorkflowitemType.HORIZONTAL.toString,
      1,
      None,
      parentWorkflow,
      None
    )


  }
}