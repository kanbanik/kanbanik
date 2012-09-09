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
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.DataLoader
import com.googlecode.kanbanik.model.Workflowitem
import com.mongodb.casbah.commons.MongoDBObject

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

      assert(Workflowitem.byId(new ObjectId("3f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === "1f48e10644ae3742baa2d0b9")

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

      assert(Workflowitem.byId(new ObjectId("1f48e10644ae3742baa2d0b9")).child.isDefined === false)
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

      assert(Workflowitem.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.isDefined === true)
      assert(Workflowitem.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.get.id.get === "9f48e10644ae3742baa2d0b9")
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

      assert(Workflowitem.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.isDefined === true)
      assert(Workflowitem.byId(new ObjectId("5f48e10644ae3742baa2d0b9")).child.get.id.get === "7f48e10644ae3742baa2d0b9")
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
      DataLoader.clearDB
      EditWorkflowDataLoader.buildWorkflow
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
      DataLoader.clearDB
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

      assert(Workflowitem.byId(new ObjectId("3f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === stored.getId())

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be possible to move from las inside to even more inside") {
      DataLoader.clearDB
      EditWorkflowDataLoader.prepareBoardAndProject

      // outer
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("1f48e10644ae3742baa2d0b9"),
        "name" -> "name1",
        "wipLimit" -> 4,
        "itemType" -> "V",
        "childId" -> Some(new ObjectId("2f48e10644ae3742baa2d0b9")),
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("2f48e10644ae3742baa2d0b9"),
        "name" -> "name2",
        "wipLimit" -> 2,
        "itemType" -> "V",
        "childId" -> None,
        "nextItemId" -> Some(new ObjectId("3f48e10644ae3742baa2d0b9")),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("3f48e10644ae3742baa2d0b9"),
        "name" -> "name3",
        "wipLimit" -> 2,
        "itemType" -> "V",
        "childId" -> None,
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

      move(
        "3f48e10644ae3742baa2d0b9",
        null,
        "2f48e10644ae3742baa2d0b9")

      val first = Workflowitem.byId(new ObjectId("1f48e10644ae3742baa2d0b9")).child.get
      assert(first.id.get.toString === "2f48e10644ae3742baa2d0b9")
      assert(first.nextItem === None)
      assert(first.child.get.id.get.toString === "3f48e10644ae3742baa2d0b9")
    }
    
     it("should be able to move from last inside to last outside") {
      // 1  ->    2     ->  3
      //    (4 -> 5 -> 6)
      // 6 moves after 3
      DataLoader.clearDB
      EditWorkflowDataLoader.prepareBoardAndProject

      // outer
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("1f48e10644ae3742baa2d0b9"),
        "name" -> "name1",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> Some(new ObjectId("2f48e10644ae3742baa2d0b9")),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("2f48e10644ae3742baa2d0b9"),
        "name" -> "name2",
        "wipLimit" -> 2,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> Some(new ObjectId("3f48e10644ae3742baa2d0b9")),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("3f48e10644ae3742baa2d0b9"),
        "name" -> "name3",
        "wipLimit" -> 2,
        "itemType" -> "H",
        "childId" -> Some(new ObjectId("4f48e10644ae3742baa2d0b9")),
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

      // inner
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("4f48e10644ae3742baa2d0b9"),
        "name" -> "name1",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> Some(new ObjectId("5f48e10644ae3742baa2d0b9")),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("5f48e10644ae3742baa2d0b9"),
        "name" -> "name2",
        "wipLimit" -> 2,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> Some(new ObjectId("6f48e10644ae3742baa2d0b9")),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("6f48e10644ae3742baa2d0b9"),
        "name" -> "name3",
        "wipLimit" -> 2,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

      moveAndCheck(
        "6f48e10644ae3742baa2d0b9",
        null,
        null,

        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")
        
        moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        null,
        null,

        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9",
        "1f48e10644ae3742baa2d0b9")
    }

    it("should be able to add new entity as the only to deeper workflow") {
      val stored = move(
        null,
        null,
        "8f48e10644ae3742baa2d0b9")

      checkOrder(
        stored.getId())

      assert(Workflowitem.byId(new ObjectId("7f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === "8f48e10644ae3742baa2d0b9")
      assert(Workflowitem.byId(new ObjectId("8f48e10644ae3742baa2d0b9")).child.get.id.get.toString() === stored.getId())

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able move to the same place") {
      moveAndCheck(
        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        null,

        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")
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
      { if (contextId == null) null else context })).getPayload().getPayload()
  }

}