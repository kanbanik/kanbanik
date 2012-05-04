package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import org.junit.runner.RunWith

import com.mongodb.casbah.commons.MongoDBObject

class BoardScalaTest extends BaseIntegrationTest {

  describe("Board should be able to do all the CRUD operations") {

    it("should return empty list when no boards are in db for all") {
      DataLoader.clearDB
      assert(BoardScala.all().size === 0)
    }

    it("should return the correct list if there is one boards for all") {
      DataLoader.clearDB
      DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("2f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> None)
        
      assert(BoardScala.all().size === 1)
    }

    it("should return the correct list if there are more boards for all") {
      DataLoader.clearDB
      DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("2f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> None)
      
      DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("3f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> None)
      
      DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("4f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> None)
      
      DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("5f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> None)
      
      assert(BoardScala.all().size === 4)
    }
    
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
    
    it("should returned the new board") {
      val board = BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.name = "renamed"
      val renamed = board.store
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