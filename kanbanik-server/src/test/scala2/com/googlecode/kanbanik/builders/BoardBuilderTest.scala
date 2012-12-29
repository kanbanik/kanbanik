package com.googlecode.kanbanik.builders

import java.util.ArrayList
import java.util.Arrays

import org.bson.types.ObjectId
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.BaseWorkflowManipulatingTest
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.model.Workflowitem

class BoardBuilderTest extends BaseWorkflowManipulatingTest with WorkflowitemTestManipulation {

  val builder = new BoardBuilder()

  "buildDto() " should "be able to build a board without workflow" in {
    val board = Board().withName("someName").withVersion(2).withWorkflow(Workflow())
    val res = builder.buildDto(board)

    assert(res.getName() === "someName")
    assert(res.getVersion() === 2)
    assert(res.getWorkflow().getWorkflowitems().size() === 0)
  }

  it should "be able to build a board without one level workflow" in {
    val workflow = Workflow(
      List(
        Workflowitem().withName("name1"),
        Workflowitem().withName("name2"),
        Workflowitem().withName("name3")))
    val board = Board().withName("someName").withVersion(2).withWorkflow(workflow)

    val res = builder.buildDto(board)

    assert(res.getName() === "someName")
    assert(res.getVersion() === 2)
    assert(res.getWorkflow().getWorkflowitems().size() === 3)
    assert(asNamesList(res.getWorkflow()) === List("name1", "name2", "name3"))

  }

  it should "be able to build a board without two level workflow" in {
    val workflow = Workflow(
      List(
        Workflowitem().withName("name1"),
        Workflowitem().withName("name2").withWorkflow(
          Workflow(
            List(
              Workflowitem().withName("inner1"),
              Workflowitem().withName("inner2"),
              Workflowitem().withName("inner3")))),
        Workflowitem().withName("name3")))
    
        val board = Board().withName("someName").withVersion(2).withWorkflow(workflow)
    val res = builder.buildDto(board)
    
    assert(res.getName() === "someName")    
    assert(res.getWorkflow().getWorkflowitems().size() === 3)
    assert(asNamesList(res.getWorkflow()) === List("name1", "name2", "name3"))
    
    assert(asNamesList(res.getWorkflow().getWorkflowitems().get(1).getNestedWorkflow()) === List("inner1", "inner2", "inner3"))
  }
  
  "buildEntity() " should "be able to build a board without a workflow" in {
    val board = new BoardDto()
    board.setName("some name")
    board.setVersion(2)
    board.setId(new ObjectId().toString())
    board.setWorkflow(new WorkflowDto())
    
    val res = builder.buildEntity(board)
    assert(res.name === "some name")
    assert(res.version === 2)
    assert(res.workflow.workflowitems == List())
    
  }
  
  "buildEntity() " should "be able to build a board without a one level workflow" in {
    val board = new BoardDto()
    board.setName("some name")
    board.setVersion(2)
    board.setId(new ObjectId().toString())
    
    
    val firstLevelWorkflow = new WorkflowDto()
    firstLevelWorkflow.setWorkflowitems(new ArrayList(Arrays.asList(
    		itemDtoWithName("upper1"),
    		itemDtoWithName("upper2"),
    		itemDtoWithName("upper3")
    )))
    
    board.setWorkflow(firstLevelWorkflow)
    
    val res = builder.buildEntity(board)
    assert(res.name === "some name")
    assert(res.version === 2)
    assert(res.workflow.workflowitems.map(_.name) == List("upper1", "upper2", "upper3"))
  }
  
  "buildEntity() " should "be able to build a board without a two level workflow" in {
    val board = new BoardDto()
    board.setName("some name")
    board.setVersion(2)
    board.setId(new ObjectId().toString())
    
    val secondLevelWorkflow = new WorkflowDto()
    
    val middleWorkflowitem = itemDtoWithName("upper2")
    middleWorkflowitem.setNestedWorkflow(secondLevelWorkflow)
    
    
    val firstLevelWorkflow = new WorkflowDto()
    firstLevelWorkflow.setWorkflowitems(new ArrayList(Arrays.asList(
    		itemDtoWithName("upper1"),
    		middleWorkflowitem,
    		itemDtoWithName("upper3")
    )))
    
    secondLevelWorkflow.setWorkflowitems(new ArrayList(Arrays.asList(
    		itemDtoWithName("unter1", firstLevelWorkflow),
    		itemDtoWithName("unter2", firstLevelWorkflow),
    		itemDtoWithName("unter3", firstLevelWorkflow)
    )))
    
    board.setWorkflow(firstLevelWorkflow)
    
    val res = builder.buildEntity(board)
    assert(res.name === "some name")
    assert(res.version === 2)
    assert(res.workflow.workflowitems.map(_.name) == List("upper1", "upper2", "upper3"))
    assert(res.workflow.workflowitems.tail.head.nestedWorkflow.workflowitems.map(_.name) == List("unter1", "unter2", "unter3"))
  }
  
  def asNamesList(res: WorkflowDto) = res.getWorkflowitems().toArray.toList.map(_.asInstanceOf[WorkflowitemDto].getName())
}