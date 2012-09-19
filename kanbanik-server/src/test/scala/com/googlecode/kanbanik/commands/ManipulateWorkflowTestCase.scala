package com.googlecode.kanbanik.commands
import org.junit.runner.RunWith
import com.googlecode.kanbanik.model.Workflowitem
import org.scalatest.BeforeAndAfter
import org.scalatest.Spec
import com.googlecode.kanbanik.model.DataLoader
import org.bson.types.ObjectId
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.ItemType

@RunWith(classOf[JUnitRunner])
class ManipulateWorkflowTestCase extends Spec with BeforeAndAfter  {
 before {
    // if the DB contained something before the test runs
    DataLoader.clearDB
    EditWorkflowDataLoader.buildWorkflow
  }

  after {
    DataLoader.clearDB
  }
  
  def assertTopLevelEntitiesInBoard(num: Int) {
    val board = Board.byId(new ObjectId("1e48e10644ae3742baa2d0b9"))
    assert(board.workflowitems.get.size === num)
  }
  
  def checkOrder(expectedIdOrder: String*) {
    expectedIdOrder.tail.foldLeft(expectedIdOrder.head)((curr: String, next: String) => {
      assert(Workflowitem.byId(new ObjectId(curr)).nextItem.get.id.get.toString() === next, curr + " should have a next: " + next)
      next
    });

    assert(Workflowitem.byId(new ObjectId(expectedIdOrder.last)).nextItem.isDefined === false, expectedIdOrder.last + " should not have a next")
  }
  
   def createFilledDto() = {
    val dto = new WorkflowitemDto
    dto.setName("")
    dto.setItemType(ItemType.HORIZONTAL)
    val board = new BoardDto
    board.setVersion(1)
    board.setId("1e48e10644ae3742baa2d0b9")
    dto.setBoard(board)
    dto
  }
}