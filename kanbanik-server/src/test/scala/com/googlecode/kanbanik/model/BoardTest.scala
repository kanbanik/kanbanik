package com.googlecode.kanbanik.model

import org.bson.types.ObjectId
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

class BoardTest extends BaseWorkflowManipulatingTest {

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


}