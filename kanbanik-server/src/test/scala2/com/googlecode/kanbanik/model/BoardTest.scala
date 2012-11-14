package com.googlecode.kanbanik.model

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.bson.types.ObjectId

@RunWith(classOf[JUnitRunner])
class BoardTest extends FlatSpec {

  val tmpBoard = new Board(None, "", 1, null)
  val a = mkWorkflowItem(new ObjectId(), Workflow(), tmpBoard)
  val b = mkWorkflowItem(new ObjectId(), Workflow(), tmpBoard)
  val c = mkWorkflowItem(new ObjectId(), Workflow(), tmpBoard)
  val workflow = Workflow(List(a, b, c))
  val board = tmpBoard.withWorkflow(workflow)

  "store() " should "should be able to store an empty board" in {
    val stored = new Board(None, "some name", 1).store
    assert(stored.id.isDefined === true)
    assert(stored.name === "some name")
    assert((stored.workflow != null) === true)
  }

  it should "should be able to a one level workflow" in {
    try {
      val stored = new Board(None, "some name", 1, workflow).store
      assert(stored.id.isDefined === true)
      assert(stored.name === "some name")
      assert((stored.workflow != null) === true)
    } catch {
      case e => e.printStackTrace()
    }
  }

  "move() on one level " should "be able to move from beginning to end" in {
    val movedBoard = board.move(a, None, workflow)
    assert(movedBoard.workflow.workflowitems === List(b, c, a))
  }

  it should "be able to move from end to beginning" in {
    val movedBoard = board.move(c, Some(a), workflow)
    assert(movedBoard.workflow.workflowitems === List(c, a, b))
  }

  it should "be able to move from middle to beginning" in {
    val movedBoard = board.move(b, Some(a), workflow)
    assert(movedBoard.workflow.workflowitems === List(b, a, c))
  }

  it should "be able to move from middle to end" in {
    val movedBoard = board.move(b, None, workflow)
    assert(movedBoard.workflow.workflowitems === List(a, c, b))
  }

  val a1 = mkWorkflowItem(new ObjectId(), Workflow())
  val b1 = mkWorkflowItem(new ObjectId(), Workflow())
  val c1 = mkWorkflowItem(new ObjectId(), Workflow())
  val workflow1 = Workflow(List(a1, b1, c1))

  val b0Id = new ObjectId()
  val b0 = mkWorkflowItem(b0Id, workflow1)
  val workflow0 = Workflow(List(a, b0, c))
  val board1 = new Board(None, "", 1, workflow0)

  "move() with two levels" should "be able to move from first outside to down" in {
    val movedBoardFirstToFirst = board1.move(a, Some(a1), workflow1)
    assert(movedBoardFirstToFirst.workflow.workflowitems === List(mkWorkflowItem(b0Id, Workflow(List(a, a1, b1, c1))), c))

    val movedBoardFirstToSecond = board1.move(a, Some(b1), workflow1)
    assert(movedBoardFirstToSecond.workflow.workflowitems === List(mkWorkflowItem(b0Id, Workflow(List(a1, a, b1, c1))), c))

    val movedBoardFirstToLast = board1.move(a, None, workflow1)
    assert(movedBoardFirstToLast.workflow.workflowitems === List(mkWorkflowItem(b0Id, Workflow(List(a1, b1, c1, a))), c))
  }

  "move() with two levels" should "be able to move from last outside to down" in {
    val movedBoardFirstToFirst = board1.move(c, Some(a1), workflow1)
    assert(movedBoardFirstToFirst.workflow.workflowitems === List(a, mkWorkflowItem(b0Id, Workflow(List(c, a1, b1, c1)))))

    val movedBoardFirstToSecond = board1.move(c, Some(b1), workflow1)
    assert(movedBoardFirstToFirst.workflow.workflowitems === List(a, mkWorkflowItem(b0Id, Workflow(List(a1, c, b1, c1)))))

    val movedBoardFirstToLast = board1.move(c, None, workflow1)
    assert(movedBoardFirstToFirst.workflow.workflowitems === List(a, mkWorkflowItem(b0Id, Workflow(List(a1, b1, c1, c)))))
  }

  def mkWorkflowItem(id: ObjectId, nestedWorkflow: Workflow, board: Board) = {
    new Workflowitem(Some(id), "", -1, "", 1, nestedWorkflow, board)
  }
  
  def mkWorkflowItem(id: ObjectId, nestedWorkflow: Workflow): Workflowitem = {
    mkWorkflowItem(id, nestedWorkflow, null)
  }

}