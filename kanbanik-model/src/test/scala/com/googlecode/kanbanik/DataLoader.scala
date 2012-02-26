package com.googlecode.kanbanik
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

object DataLoader {

  val workflowitems = MongoConnection()("kanbanik")("workflowitems")
  val boards = MongoConnection()("kanbanik")("boards")

  def fillDB() {
    fillWorkflowitems()
    fillComplexWorkflowitems()
    fillBoards()
  }

  def clearDB() {
    workflowitems.find().foreach {
      workflowitems.remove(_)
    }

    boards.find().foreach {
      boards.remove(_)
    }
  }

  private def fillComplexWorkflowitems() {
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("1c48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> List(
        MongoDBObject(
          "_id" -> new ObjectId("2c48e10644ae3742baa2d0d9"),
          "name" -> "some name",
          "wipLimit" -> 4,
          "children" -> null,
          "nextItemId" -> null,
          "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d9")),
        MongoDBObject(
          "_id" -> new ObjectId("3c48e10644ae3742baa2d0d9"),
          "name" -> "some name",
          "wipLimit" -> 4,
          "children" -> List(
            MongoDBObject(
              "_id" -> new ObjectId("4c48e10644ae3742baa2d0d9"),
              "name" -> "some name",
              "wipLimit" -> 4,
              "children" -> null,
              "nextItemId" -> null,
              "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d9"))),
          "nextItemId" -> null,
          "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d9"))),
      "nextItemId" -> null,
      "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d9"))
  }

  private def fillBoards() {
    boards += MongoDBObject(
      "_id" -> new ObjectId("1f48e10644ae3742baa2d0b9"),
      "name" -> "board1 name",
      "workflowitems" -> null)

    boards += MongoDBObject(
      "_id" -> new ObjectId("4f48e10644ae3742baa2d0d0"),
      "name" -> "board1 name",
      "workflowitems" -> List(
          new ObjectId("1f48e10644ae3742baa2d0d9"), 
          new ObjectId("2f48e10644ae3742baa2d0d9"), 
          new ObjectId("3f48e10644ae3742baa2d0d9")
       ))
  }

  private def fillWorkflowitems() {
    // for basic read write of workflowitems
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("4f48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> null,
      "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d9"))

    // for workflowitem moving - three items
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("1f48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("2f48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("2f48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("3f48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("3f48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> null,
      "boardId" -> new ObjectId("4f48e10644ae3742baa2d0d0"))

    // for workflowitem moving - bigger board
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("1a48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("2a48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("5f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("2a48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("3a48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("5f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("3a48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("4a48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("5f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("4a48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("5a48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("5f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("5a48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("6a48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("5f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("6a48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> null,
      "boardId" -> new ObjectId("5f48e10644ae3742baa2d0d0"))

    // for workflowitem moving - two element board
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("1b48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> new ObjectId("2b48e10644ae3742baa2d0d9"),
      "boardId" -> new ObjectId("6f48e10644ae3742baa2d0d0"))
    workflowitems += MongoDBObject(
      "_id" -> new ObjectId("2b48e10644ae3742baa2d0d9"),
      "name" -> "some name",
      "wipLimit" -> 4,
      "children" -> null,
      "nextItemId" -> null,
      "boardId" -> new ObjectId("6f48e10644ae3742baa2d0d0"))
  }
}