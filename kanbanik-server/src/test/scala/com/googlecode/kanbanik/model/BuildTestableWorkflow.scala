package com.googlecode.kanbanik.model
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

// This actually tests nothing, only prepares the DB to
// contain some data
@RunWith(classOf[JUnitRunner])
class BuildTestableWorkflow extends Spec {

  describe("This actually tests nothing, only prepares the DB to contain some data") {

    it("should create a complex data structure with all the features") {
      DataLoader.clearDB
      // boards
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

      DataLoader.projects += MongoDBObject(
        "_id" -> new ObjectId("2a48e10644ae3742baa2d0d9"),
        "name" -> "project2",
        "boards" -> List(
          new ObjectId("1e48e10644ae3742baa2d0b9")),
        "tasks" -> List(
        		new ObjectId("1a48e10644ae3742baa2d1d9"),
        		new ObjectId("2a48e10644ae3742baa2d1d9")
        ))

      // workflowitems    
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("3f48e10644ae3742baa2d0b9"),
        "name" -> "name1",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> Some(new ObjectId("4f48e10644ae3742baa2d0b9")),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("4f48e10644ae3742baa2d0b9"),
        "name" -> "name2",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> new ObjectId("5f48e10644ae3742baa2d0b9"),
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("5f48e10644ae3742baa2d0b9"),
        "name" -> "name2-1",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> new ObjectId("6f48e10644ae3742baa2d0b9"),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("6f48e10644ae3742baa2d0b9"),
        "name" -> "name2-2",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> new ObjectId("6f48e10644ae3742baa2d0b0"),
        "nextItemId" -> new ObjectId("7f48e10644ae3742baa2d0b9"),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("7f48e10644ae3742baa2d0b9"),
        "name" -> "name2-3",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("6f48e10644ae3742baa2d0b0"),
        "name" -> "name2-2-1",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> new ObjectId("6f48e10644ae3742baa2d0b1"),
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))
      DataLoader.workflowitems += MongoDBObject(
        "_id" -> new ObjectId("6f48e10644ae3742baa2d0b1"),
        "name" -> "name2-2-2",
        "wipLimit" -> 4,
        "itemType" -> "H",
        "childId" -> None,
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

      // tasks
      DataLoader.tasks += MongoDBObject(
        "_id" -> new ObjectId("1a48e10644ae3742baa2d1d9"),
        "name" -> "task name1",
        "description" -> "task description",
        "classOfService" -> 1,
        "ticketId" -> "generated id",
        "workflowitem" -> new ObjectId("6f48e10644ae3742baa2d0b0"))

      DataLoader.tasks += MongoDBObject(
        "_id" -> new ObjectId("2a48e10644ae3742baa2d1d9"),
        "name" -> "task name2",
        "description" -> "task description",
        "classOfService" -> 1,
        "ticketId" -> "generated id",
        "workflowitem" -> new ObjectId("6f48e10644ae3742baa2d0b0"))
    }

  }

}