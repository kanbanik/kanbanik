package com.googlecode.kanbanik
import org.bson.types.ObjectId

class BoardScalaTest extends BaseIntegrationTest {

  describe("Board should be able to do all the CRUD operations") {
    it("should be able to retrive the entity using id") {
      val loaded = BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      assert(loaded.name === "board1 name")
      assert(loaded.workflowitems === None)
    }

    it("should be able to retrive also the workflowitems using id") {
      val loaded = BoardScala.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      assert(loaded.name === "board1 name")
      assert(loaded.workflowitems.getOrElse(fail("None workflowitems, but expected some")).size === 2)
    }

    it("should fail when not existing id is requested") {
      intercept[IllegalArgumentException] {
        BoardScala.byId(new ObjectId("2f48e10644ae3742baa2d0c9"))
      }
    }

    it("should be possible to store board without workflow") {
      val stored = new BoardScala(None, "stored", None).store
      assert(BoardScala.byId(stored.id.getOrElse(notSet)).name === "stored")
    }

    it("should be possible to store board with workflow") {
      val stored = new BoardScala(None, "stored",
        Some(List(
          WorkflowitemScala.byId(new ObjectId("1f48e10644ae3742baa2d0d9")),
          WorkflowitemScala.byId(new ObjectId("2f48e10644ae3742baa2d0d9"))))).store
      assert(BoardScala.byId(stored.id.getOrElse(notSet)).name === "stored")
    }

    it("should be possible to update the name of the board") {
      val board = BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.name = "renamed"
      board.store
      val renamed = BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      assert(renamed.name === "renamed")
    }

    it("should be possible to delete a board") {
      val board = BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.delete
      intercept[IllegalArgumentException] {
        BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      }
    }

    it("should be possible to remove workflow from the board") {
      val board = BoardScala.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      board.workflowitems = Some(List(board.workflowitems.get(0)))
      board.store
      val withOneWorkflowitemOnly = BoardScala.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      assert(withOneWorkflowitemOnly.workflowitems.get.size === 1)
    }
    
    it("should be possible to add workflow to the board") {
      val board = BoardScala.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      board.workflowitems = Some(WorkflowitemScala.byId(new ObjectId("4f48e10644ae3742baa2d0d9")) :: board.workflowitems.get)
      board.store
      val withOneWorkflowitemOnly = BoardScala.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      assert(withOneWorkflowitemOnly.workflowitems.get.size === 3)
    }
    
  }

  def notSet = throw new IllegalStateException("Required value not set");
}