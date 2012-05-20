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
    EditWorkflowDataLoader.buildWorkflow
  }

  after {
    DataLoader.clearDB
  }

  describe("This command should take care of editing of the workflow") {

    it("should be able to move the workflowitem from outside to inside") {
//      moveAndCheck(
//        "1f48e10644ae3742baa2d0b9",
//        null,
//        null,
//        "2f48e10644ae3742baa2d0b9",
//
//        "4f48e10644ae3742baa2d0b9",
//        "5f48e10644ae3742baa2d0b9",
//        "6f48e10644ae3742baa2d0b9",
//        "1f48e10644ae3742baa2d0b9")
//
//      checkOrder(
//        "2f48e10644ae3742baa2d0b9",
//        "3f48e10644ae3742baa2d0b9")
    }

    it("should be able to move a workflowitem from inside to outside") {

      moveAndCheck(
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        null,
        null,
        
        "1f48e10644ae3742baa2d0b9",
        "4f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      checkOrder(
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      val board = BoardScala.byId(new ObjectId("1e48e10644ae3742baa2d0b9"))
      assert(board.workflowitems.get.size === 4)
    }

  }

  private def moveAndCheck(currentId: String, nextId: String, parentId: String, contextId: String, expectedIdOrder: String*) {
    val current = createFilledDto()
    current.setId(currentId)

    val next = createFilledDto()
    next.setId(nextId)

    val parent = createFilledDto()
    parent.setId(parentId)
    
    val context = createFilledDto()
    context.setId(contextId)
    
    current.setNextItem(next)
    command.execute(new EditWorkflowParams(
        {if (parentId == null) null else parent}, 
        current, 
        {if (contextId == null) null else context}))

    checkOrder(expectedIdOrder.toList : _*)
  }

  private def checkOrder(expectedIdOrder: String*) {
    expectedIdOrder.tail.foldLeft(expectedIdOrder.head)((curr: String, next: String) => {
      println(WorkflowitemScala.byId(new ObjectId(curr)).id.get.toString() + "->" + next)
      assert(WorkflowitemScala.byId(new ObjectId(curr)).nextItem.get.id.get.toString() === next, curr + " should have a next: " + next)
      println("done")
      next
    });
    
    assert(WorkflowitemScala.byId(new ObjectId(expectedIdOrder.last)).nextItem.isDefined === false, expectedIdOrder.last + "should not have a next")
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