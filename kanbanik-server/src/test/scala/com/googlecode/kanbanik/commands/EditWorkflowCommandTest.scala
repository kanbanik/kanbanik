package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.BaseIntegrationTest
import com.googlecode.kanbanik.model.DataLoader
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.WorkflowitemScala

class EditWorkflowCommandTest extends BaseIntegrationTest {
  
  val command = new EditWorkflowCommand
  
  describe("This command should take care of editing of the workflow") {
    it("should be able to move a workflowitem from inside to outside") {
      DataLoader.clearDB
      buildSomeData()
      
      val current = createFilledDto()
      current.setId("5f48e10644ae3742baa2d0b9")
      
      val next = createFilledDto()
      next.setId("3f48e10644ae3742baa2d0b9")
      
      current.setNextItem(next)
      command.execute(new EditWorkflowParams(null, current))
      
      val item = WorkflowitemScala.byId(new ObjectId("5f48e10644ae3742baa2d0b9"))
      assert(item.id.get.toString() === "5f48e10644ae3742baa2d0b9")
      assert(item.nextItem.get.id.get.toString === "3f48e10644ae3742baa2d0b9")
      assert(item.nextItem.get.nextItem.get.id.get.toString === "4f48e10644ae3742baa2d0b9")
    }
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

  private def buildSomeData() {
    DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("1e48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> List(
        new ObjectId("3f48e10644ae3742baa2d0b9"),
        new ObjectId("4f48e10644ae3742baa2d0b9")))

    DataLoader.projects += MongoDBObject(
      "_id" -> new ObjectId("1a48e10644ae3742baa2d0d9"),
      "name" -> "project1",
      "boards" -> List(
        new ObjectId("1e48e10644ae3742baa2d0b9")),
      "tasks" -> None)

    // workflowitems    
    DataLoader.workflowitems += MongoDBObject(
      "_id" -> new ObjectId("3f48e10644ae3742baa2d0b9"),
      "name" -> "name1",
      "wipLimit" -> 4,
      "itemType" -> "V",
      "childId" -> None,
      "nextItemId" -> Some(new ObjectId("4f48e10644ae3742baa2d0b9")),
      "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
    DataLoader.workflowitems += MongoDBObject(
      "_id" -> new ObjectId("4f48e10644ae3742baa2d0b9"),
      "name" -> "name2",
      "wipLimit" -> 2,
      "itemType" -> "V",
      "childId" -> Some(new ObjectId("5f48e10644ae3742baa2d0b9")),
      "nextItemId" -> None,
      "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
    DataLoader.workflowitems += MongoDBObject(
      "_id" -> new ObjectId("5f48e10644ae3742baa2d0b9"),
      "name" -> "name2-1",
      "wipLimit" -> 2,
      "itemType" -> "V",
      "childId" -> None,
      "nextItemId" -> None,
      "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

  }
}