package com.googlecode.kanbanik.model
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

@RunWith(classOf[JUnitRunner])
class BuildSimpleTestableWorkflow extends Spec {
  describe("This actually tests nothing, only prepares the DB to contain some data") {

    it("should create a simple workflow") {
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
        "name" -> "name3",
        "wipLimit" -> 2,
        "itemType" -> "V",
        "childId" -> None,
        "nextItemId" -> None,
        "boardId" -> new ObjectId("1e48e10644ae3742baa2d0b9"))

    }

  }
}