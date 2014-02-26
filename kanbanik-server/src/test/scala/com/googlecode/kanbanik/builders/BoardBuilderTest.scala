package com.googlecode.kanbanik.builders

import org.bson.types.ObjectId
import com.googlecode.kanbanik.dtos.{BoardDto, WorkflowDto}
import com.googlecode.kanbanik.model.BaseWorkflowManipulatingTest
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.model.Workflowitem

class BoardBuilderTest extends BaseWorkflowManipulatingTest with WorkflowitemTestManipulation {

  val builder = new BoardBuilder()

  "buildDto() " should "be able to build a board without workflow" in {
    val board = Board().copy(name = "someName", version = 2, workflow = Workflow())
    val res = builder.buildDto(board)

    assert(res.name === "someName")
    assert(res.version === 2)
    assert(res.workflow.get.workflowitems.get.size === 0)
  }

  it should "be able to build a board with one level workflow" in {
    val workflow = Workflow(
      List(
        Workflowitem().copy(name = "name1"),
        Workflowitem().copy(name = "name2"),
        Workflowitem().copy(name = "name3")))
    val board = Board().copy(name = "someName", version = 2, workflow = workflow)

    val res = builder.buildDto(board)

    assert(res.name === "someName")
    assert(res.version === 2)
    assert(res.workflow.get.workflowitems.get.size === 3)
    assert(asNamesList(res.workflow.get) === List("name1", "name2", "name3"))

  }

  it should "be able to build a board with two level workflow" in {
    val workflow = Workflow(
      List(
        Workflowitem().copy(name = "name1"),
        Workflowitem().copy(name = "name2").copy(nestedWorkflow =
          Workflow(
            List(
              Workflowitem().copy(name = "inner1"),
              Workflowitem().copy(name = "inner2"),
              Workflowitem().copy(name = "inner3")))),
        Workflowitem().copy(name = "name3")))

    val board = Board().copy(name = "someName", version = 2, workflow = workflow)
    val res = builder.buildDto(board)

    assert(res.name === "someName")
    assert(res.workflow.get.workflowitems.get.size === 3)
    assert(asNamesList(res.workflow.get) === List("name1", "name2", "name3"))

    assert(asNamesList(res.workflow.get.workflowitems.get(1).nestedWorkflow.get) === List("inner1", "inner2", "inner3"))
  }

  "buildEntity() " should "be able to build a board without a workflow" in {
    val board = new BoardDto(
      Some(new ObjectId().toString()),
      2,
      "some name",
      -1,
      None,
      Some(false),
      None
    )

    val res = builder.buildEntity(board)
    assert(res.name === "some name")
    assert(res.version === 2)
    assert(res.workflow.workflowitems == List())

  }

  "buildEntity() " should "be able to build a board with a one level workflow" in {
    val firstLevelWorkflow = new WorkflowDto(
      null,
      Some(List(
        itemDtoWithName("upper1"),
        itemDtoWithName("upper2"),
        itemDtoWithName("upper3")
      )),
      null

    )

    val board = new BoardDto(
      Some(new ObjectId().toString()),
      2,
      "some name",
      -1,
      Some(firstLevelWorkflow),
      Some(false),
      None
    )

    val res = builder.buildEntity(board)
    assert(res.name === "some name")
    assert(res.version === 2)
    assert(res.workflow.workflowitems.map(_.name) == List("upper1", "upper2", "upper3"))
  }

  "buildEntity() " should "be able to build a board with a two level workflow" in {

    val firstLevelWorkflow = WorkflowDto(
      None,
      Some(List(
        itemDtoWithName("upper1"),
        itemDtoWithName("upper2").copy(nestedWorkflow = Some(
          WorkflowDto(
            None,
            Some(List(
              itemDtoWithName("unter1", Some(WorkflowDto(None, None, null))),
              itemDtoWithName("unter2", Some(WorkflowDto(None, None, null))),
              itemDtoWithName("unter3", Some(WorkflowDto(None, None, null)))
            )),
            null
          )
        )),
        itemDtoWithName("upper3")
      )),
      null
    )

    val board = new BoardDto(
      Some(new ObjectId().toString()),
      2,
      "some name",
      -1,
      Some(firstLevelWorkflow),
      Some(false),
      None
    )

    val res = builder.buildEntity(board)
    assert(res.name === "some name")
    assert(res.version === 2)
    assert(res.workflow.workflowitems.map(_.name) == List("upper1", "upper2", "upper3"))
    assert(res.workflow.workflowitems.tail.head.nestedWorkflow.workflowitems.map(_.name) == List("unter1", "unter2", "unter3"))
  }

  def asNamesList(res: WorkflowDto) = res.workflowitems.get.map(_.name)
}