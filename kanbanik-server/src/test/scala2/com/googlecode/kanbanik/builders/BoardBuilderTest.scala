package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.model.BaseWorkflowManipulatingTest
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.WorkflowDto

class BoardBuilderTest extends BaseWorkflowManipulatingTest {

  val builder = new BoardBuilder()

  "buildDto() " should "be able to build a board without workflow" in {
    val board = Board().withName("someName").withVersion(2)
    val res = builder.buildDto(board)

    assert(res.getName() === "someName")
    assert(res.getVersion() === 2)
    assert(res.getWorkflow().getWorkflowitems().size() === 0)
  }

  "buildDto() " should "be able to build a board without one level workflow" in {
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

  "buildDto() " should "be able to build a board without two level workflow" in {
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

  def asNamesList(res: WorkflowDto) = res.getWorkflowitems().toArray.toList.map(_.asInstanceOf[WorkflowitemDto].getName())
}