package com.googlecode.kanbanik.model

class BoardLiveTest extends BaseWorkflowManipulatingTest {

  "store() " should "should be able to store an empty board" in {
    val stored = new Board(None, "some name", 1).store
    assert(stored.id.isDefined === true)
    assert(stored.name === "some name")
    assert((stored.workflow != null) === true)
  }

  it should "should be able to store a one level workflow" in {
    val storedBoard = new Board(None, "someName", 1).store
    val storedWorkflowWithItems = storedBoard.workflow.
      addItem(a, None).
      addItem(b, None).
      addItem(c, None)

    val storedBoardWithItems = storedBoard.withWorkflow(storedWorkflowWithItems).store
    val loadedBoard = Board.byId(storedBoardWithItems.id.get)

    assert(loadedBoard.id.isDefined === true)
    assert(loadedBoard.name === "someName")
    assert(loadedBoard.workflow.workflowitems.map(_.id) === List(a.id, b.id, c.id))
  }

  it should "should be able to store a two level workflow" in {
    val storedBoard = new Board(None, "someName", 1).store
    
    val storedWorkflowWithItems = storedBoard.workflow.
      addItem(a, None).
      addItem(b0, None).
      addItem(c, None)

    val storedBoardWithItems = storedBoard.withWorkflow(storedWorkflowWithItems).store
    val loadedBoard = Board.byId(storedBoardWithItems.id.get)

    assert(loadedBoard.id.isDefined === true)
    assert(loadedBoard.name === "someName")
    
    // first level
    assert(loadedBoard.workflow.workflowitems.map(_.id) === List(a.id, b0.id, c.id))
    
    // second level
    val secondElement = loadedBoard.workflow.workflowitems.tail.head
    assert(secondElement.nestedWorkflow.workflowitems.map(_.id) === List(a1.id, b1.id, c1.id))
  }
}
