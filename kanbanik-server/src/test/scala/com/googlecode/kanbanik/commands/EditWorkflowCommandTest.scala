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

class EditWorkflowCommandTest extends ManipulateWorkflowTestCase {

  val command = new EditWorkflowCommand

  describe("This command should take care of editing of the workflow") {

    it("should be able to move the workflowitem from outside to inside to first") {
      moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",

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
        "2f48e10644ae3742baa2d0b9",

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
        "2f48e10644ae3742baa2d0b9",

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

    it("should be able to add new entity to the start of the top-level workflow") {
      val stored = move(
        null,
        "1f48e10644ae3742baa2d0b9",
        null)

      checkOrder(
        stored.getId(),
        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(4)
    }

    it("should be able to add new entity to the end of the top-level workflow") {
      val stored = move(
        null,
        null,
        null)

      checkOrder(
        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",
        stored.getId())

      assertTopLevelEntitiesInBoard(4)
    }

    it("should be able to add new entity to the middle of the top-level workflow") {
      val stored = move(
        null,
        "2f48e10644ae3742baa2d0b9",
        null)

      checkOrder(
        "1f48e10644ae3742baa2d0b9",
        stored.getId(),
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(4)
    }

    it("should be able to add new entity to the middle of the bottom-level workflow") {
      val stored = move(
        null,
        "5f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9")

      checkOrder(
        "4f48e10644ae3742baa2d0b9",
        stored.getId(),
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able to add new entity to the start of the bottom-level workflow") {
      val stored = move(
        null,
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9")

      checkOrder(
        stored.getId(),
        "4f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able to add new entity to the end of the bottom-level workflow") {
      val stored = move(
        null,
        null,
        "2f48e10644ae3742baa2d0b9")

      checkOrder(
        "4f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9",
        stored.getId())

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able to add new entity as the only to workflow") {
      val stored = move(
        null,
        null,
        "3f48e10644ae3742baa2d0b9")

      checkOrder(
        stored.getId())

      assert(WorkflowitemScala.byId(new ObjectId("3f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === stored.getId())

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able to add new entity as the only to deeper workflow") {
      val stored = move(
        null,
        null,
        "8f48e10644ae3742baa2d0b9")

      checkOrder(
        stored.getId())

      assert(WorkflowitemScala.byId(new ObjectId("7f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === "8f48e10644ae3742baa2d0b9")
      assert(WorkflowitemScala.byId(new ObjectId("8f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === stored.getId())

      assertTopLevelEntitiesInBoard(3)
    }
  }


  private def moveAndCheck(currentId: String, nextId: String, contextId: String, expectedIdOrder: String*) {
    move(currentId, nextId, contextId);

    checkOrder(expectedIdOrder.toList: _*)
  }

  private def move(currentId: String, nextId: String, contextId: String) = {
    val current = createFilledDto()
    current.setId(currentId)

    val next = createFilledDto()
    next.setId(nextId)

    val context = createFilledDto()
    context.setId(contextId)

    current.setNextItem(next)
    command.execute(new EditWorkflowParams(
      current,
      { if (contextId == null) null else context })).getPayload()
  }


}