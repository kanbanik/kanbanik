package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter
import org.scalatest.Spec

import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.model.BaseIntegrationTest
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.DataLoader
import com.googlecode.kanbanik.model.WorkflowitemScala

@RunWith(classOf[JUnitRunner])
class EditWorkflowCommandTest extends Spec with BeforeAndAfter {

  val command = new EditWorkflowCommand

  before {
    // if the DB contained something before the test runs
    DataLoader.clearDB
    EditWorkflowDataLoader.buildWorkflow
  }

  after {
    DataLoader.clearDB
  }

  describe("This command should take care of editing of the workflow") {

    it("should be able to move the workflowitem from outside to inside to first") {
      moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",

        "1f48e10644ae3742baa2d0b9",
        "4f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      checkOrder(
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(2)
    }

    it("should be able to move the workflowitem from outside to inside to middle") {
      moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        null,
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",

        "4f48e10644ae3742baa2d0b9",
        "1f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      checkOrder(
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(2)
    }

    it("should be able to move the workflowitem from outside to inside last") {
      moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        null,
        null,
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",

        "4f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9",
        "1f48e10644ae3742baa2d0b9")

      checkOrder(
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(2)
    }

    it("should be able to move a workflowitem from first inside to outside") {

      moveAndCheck(
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        null,
        null,
        "3f48e10644ae3742baa2d0b9",

        "1f48e10644ae3742baa2d0b9",
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      checkOrder(
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(4)
    }

    it("should be able to move a workflowitem from middle inside to outside") {

      moveAndCheck(
        "5f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        null,
        null,
        "3f48e10644ae3742baa2d0b9",

        "1f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      checkOrder(
        "4f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(4)
    }

    it("should be able to move a workflowitem from last inside to outside") {

      moveAndCheck(
        "6f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        null,
        null,
        "3f48e10644ae3742baa2d0b9",

        "1f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      checkOrder(
        "4f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(4)
    }

    it("should be able to move the workflowitem from outside to be the only inside") {
      moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        null,
        "3f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",
        null,

        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(2)

      assert(WorkflowitemScala.byId(new ObjectId("3f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === "1f48e10644ae3742baa2d0b9")

    }

    it("should be able to move the last workflowitem from inside to outside") {
      moveAndCheck(
        "7f48e10644ae3742baa2d0b9",
        "1f48e10644ae3742baa2d0b9",
        null,
        null,
        "2f48e10644ae3742baa2d0b9",

        "7f48e10644ae3742baa2d0b9",
        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(4)

      assert(WorkflowitemScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9")).child.isDefined === false)
    }

    it("should be able to move the middle child to last child") {
      moveAndCheck(
        "7f48e10644ae3742baa2d0b9",
        null,
        "5f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9",

        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)

      checkOrder(
        "9f48e10644ae3742baa2d0b9",
        "7f48e10644ae3742baa2d0b9")

      assert(WorkflowitemScala.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.isDefined === true)
      assert(WorkflowitemScala.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.get.id.get === "9f48e10644ae3742baa2d0b9")
    }

    it("should be able to move the middle child to first child") {
      moveAndCheck(
        "7f48e10644ae3742baa2d0b9",
        "9f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9",

        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)

      checkOrder(
        "7f48e10644ae3742baa2d0b9",
        "9f48e10644ae3742baa2d0b9")

      assert(WorkflowitemScala.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.isDefined === true)
      assert(WorkflowitemScala.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.get.id.get === "7f48e10644ae3742baa2d0b9")
    }

  }

  private def assertTopLevelEntitiesInBoard(num: Int) {
    val board = BoardScala.byId(new ObjectId("1e48e10644ae3742baa2d0b9"))
    assert(board.workflowitems.get.size === num)
  }

  private def moveAndCheck(currentId: String, nextId: String, parentId: String, contextId: String, nextOfParentId: String, expectedIdOrder: String*) {
    val current = createFilledDto()
    current.setId(currentId)

    val next = createFilledDto()
    next.setId(nextId)

    val nextOfParent = createFilledDto();
    nextOfParent.setId(nextOfParentId)

    val parent = createFilledDto()
    parent.setId(parentId)
    parent.setNextItem(nextOfParent)

    val context = createFilledDto()
    context.setId(contextId)

    current.setNextItem(next)
    command.execute(new EditWorkflowParams(
      { if (parentId == null) null else parent },
      current,
      { if (contextId == null) null else context }))

    checkOrder(expectedIdOrder.toList: _*)
  }

  private def checkOrder(expectedIdOrder: String*) {
    expectedIdOrder.tail.foldLeft(expectedIdOrder.head)((curr: String, next: String) => {
      assert(WorkflowitemScala.byId(new ObjectId(curr)).nextItem.get.id.get.toString() === next, curr + " should have a next: " + next)
      next
    });

    assert(WorkflowitemScala.byId(new ObjectId(expectedIdOrder.last)).nextItem.isDefined === false, expectedIdOrder.last + " should not have a next")
  }

  private def createFilledDto() = {
    val dto = new WorkflowitemDto
    dto.setName("")
    dto.setItemType(ItemType.HORIZONTAL)
    val board = new BoardDto
    board.setId("1e48e10644ae3742baa2d0b9")
    dto.setBoard(board)
    dto
  }

}