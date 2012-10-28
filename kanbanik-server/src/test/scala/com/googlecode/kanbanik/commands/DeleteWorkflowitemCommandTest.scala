package com.googlecode.kanbanik.commands
import org.scalatest.BeforeAndAfter
import org.scalatest.Spec
import com.googlecode.kanbanik.model.DataLoader
import com.googlecode.kanbanik.model.Workflowitem
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.mongodb.casbah.commons.MongoDBObject
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.VoidParams

class DeleteWorkflowitemCommandTest extends ManipulateWorkflowTestCase {

  lazy val deleteCommand = new DeleteWorkflowitemCommand()

  describe("This command should take care of editing of the workflow") {

    it("should be able to delete the first top-level item") {
      delete("8f48e10644ae3742baa2d0b9")
      delete("7f48e10644ae3742baa2d0b9", 2)
      val res = delete("1f48e10644ae3742baa2d0b9", 3)

      assert(res.isSucceeded() === true)

      checkOrder(
        "2f48e10644ae3742baa2d0b9",
        "3f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(2)
    }

    it("should be able to delete the last top-level item") {
      val res = delete("3f48e10644ae3742baa2d0b9")

      assert(res.isSucceeded() === true)

      checkOrder(
        "1f48e10644ae3742baa2d0b9",
        "2f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(2)
    }

    it("should be able to delete the last bottom-level item") {
      val res = delete("6f48e10644ae3742baa2d0b9")

      assert(res.isSucceeded() === true)

      checkOrder(
        "4f48e10644ae3742baa2d0b9",
        "5f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able to delete the first bottom-level item") {
      val res = delete("4f48e10644ae3742baa2d0b9")

      assert(res.isSucceeded() === true)

      checkOrder(
        "5f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)
    }

    it("should be able to delete the middle bottom-level item") {
      delete("9f48e10644ae3742baa2d0b9")
      val res = delete("5f48e10644ae3742baa2d0b9", 2)

      assert(res.isSucceeded() === true)

      checkOrder(
        "4f48e10644ae3742baa2d0b9",
        "6f48e10644ae3742baa2d0b9")

      assertTopLevelEntitiesInBoard(3)
    }

    it("should NOT be able to delete the when have a child") {
      val res = delete("1f48e10644ae3742baa2d0b9")

      assert(res.isSucceeded() === false)

    }

    it("should NOT be able to delete the when have a task") {

      DataLoader.tasks += MongoDBObject(
        "_id" -> new ObjectId("9f48e10644ae3742baa2d0c9"),
        "name" -> "task name",
        "description" -> "task description",
        "classOfService" -> 1,
        "workflowitem" -> new ObjectId("9f48e10644ae3742baa2d0b9"))

      val res = delete("1f48e10644ae3742baa2d0b9")

      assert(res.isSucceeded() === false)

    }

  }
  
  private def delete(id: String): FailableResult[VoidParams] = {
    delete(id, 1)
  }
  
  private def delete(id: String, boardVersion: Int): FailableResult[VoidParams] = {
    val toDelete = createFilledDto(boardVersion)
    toDelete.setId(id)
    deleteCommand.execute(new SimpleParams(toDelete))
  }
}