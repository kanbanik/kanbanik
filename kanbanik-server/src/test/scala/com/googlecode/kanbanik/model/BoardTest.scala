package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import org.junit.runner.RunWith

import com.mongodb.casbah.commons.MongoDBObject

class BoardTest extends BaseIntegrationTest {

  describe("Board should be able to do all the CRUD operations") {

    it("should return empty list when no boards are in db for all") {
      DataLoader.clearDB
      assert(Board.all().size === 0)
    }

    it("should return the correct list if there is one boards for all") {
      DataLoader.clearDB
      DataLoader.boards += MongoDBObject(
      "_id" -> new ObjectId("2f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> None)
        
      assert(Board.all().size === 1)
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
      
      assert(Board.all().size === 4)
    }
    
    it("should be able to retrive the entity using id") {
      val loaded = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      assert(loaded.name === "board1 name")
      assert(loaded.workflowitems === None)
    }

    it("should be able to retrive also the workflowitems using id") {
      val loaded = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      assert(loaded.name === "board1 name")
      assert(loaded.workflowitems.getOrElse(fail("None workflowitems, but expected some")).size === 2)
    }

    it("should fail when not existing id is requested") {
      intercept[IllegalArgumentException] {
        Board.byId(new ObjectId("2f48e10644ae3742baa2d0c9"))
      }
    }

    it("should be possible to store board without workflow") {
      val stored = new Board(None, "stored", 1, 1, false, None).store
      assert(Board.byId(stored.id.getOrElse(notSet)).name === "stored")
    }

    it("should be possible to store board with workflow") {
      val stored = new Board(None, "stored", 1, 1, false,
        Some(List(
          Workflowitem.byId(new ObjectId("1f48e10644ae3742baa2d0d9")),
          Workflowitem.byId(new ObjectId("2f48e10644ae3742baa2d0d9"))))).store
      assert(Board.byId(stored.id.getOrElse(notSet)).name === "stored")
    }

    it("should be possible to update the name of the board") {
      val board = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.name = "renamed"
      board.store
      val renamed = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      assert(renamed.name === "renamed")
    }
    
    it("should returned the new board") {
      val board = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.name = "renamed"
      val renamed = board.store
      assert(renamed.name === "renamed")
    }

    it("should be possible to delete a board") {
      val board = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.delete
      intercept[IllegalArgumentException] {
        Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      }
    }
    
    it("should fail when deleteing a modified board") {
      val board = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      val boardToModify = Board.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))
      board.store
      intercept[MidAirCollisionException] {
    	  board.delete
      }
    }

    it("should be possible to remove workflow from the board") {
      val board = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      board.workflowitems = Some(List(board.workflowitems.get(0)))
      board.store
      val withOneWorkflowitemOnly = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      assert(withOneWorkflowitemOnly.workflowitems.get.size === 1)
    }

    it("should be possible to add workflow to the board") {
      val board = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      board.workflowitems = Some(Workflowitem.byId(new ObjectId("4f48e10644ae3742baa2d0d9")) :: board.workflowitems.get)
      board.store
      val withOneWorkflowitemOnly = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      assert(withOneWorkflowitemOnly.workflowitems.get.size === 3)
    }
    
    it("should throw exception on mid-air collision") {
      val board1 = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      val board2 = Board.byId(new ObjectId("2f48e10644ae3742baa2d0b9"))
      
      board1.store
      intercept[MidAirCollisionException] {
    	  board2.store
      }
    }

  }

  def notSet = throw new IllegalStateException("Required value not set");
}