package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import org.junit.runner.RunWith
import com.mongodb.casbah.commons.MongoDBObject

class ProjectTest extends BaseIntegrationTest {
  describe("Project should be able to do all the basic CRUD operations") {

    it("should return empty list when no project are in db for all") {
      DataLoader.clearDB
      assert(Project.all().size === 0)
    }

    it("should return the correct list if there is one project for all") {
      DataLoader.clearDB
      DataLoader.projects += MongoDBObject(
      "_id" -> new ObjectId("1a48e10644ae3742baa2d0d9"),
      "name" -> "project name",
      "boards" -> None,
      "tasks" -> None)
        
      assert(Project.all().size === 1)
    }
    
    it("should be possible to retrive a project without task and board") {
      val loaded = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "project name")
      assert(loaded.boards.isDefined === false)
      assert(loaded.tasks.isDefined === false)
    }

    it("should be possible to retrive a project without tasks and with boards") {
      val loaded = Project.byId(new ObjectId("2a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "project name")
      assert(loaded.boards.get.size === 2)
      assert(loaded.tasks.isDefined === false)
    }

    it("should be possible to retrive a project with tasks and with boards") {
      val loaded = Project.byId(new ObjectId("3a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "project name")
      assert(loaded.boards.get.size === 2)
      assert(loaded.tasks.get.size === 2)
    }

    it("should be possible to create a new entity without boards and without tasks") {
      val stored = new Project(None,
        "some name",
        1,
        None,
        None).store

      assert(stored.name === "some name")
    }

    it("should be possible to create a new entity without boards and with tasks") {
      val stored = new Project(None,
        "some name",
        1,
        None,
        Some(List(
          Task.byId(new ObjectId("4f48e10644ae3742baa2d0a9")),
          Task.byId(new ObjectId("5f48e10644ae3742baa2d0a9"))))).store

      assert(stored.tasks.get.size === 2)
    }

    it("should be possible to create a new entity with boards and without tasks") {
      val stored = new Project(None,
        "some name",
        1,
        Some(List(
          Board.byId(new ObjectId("1b48e10644ae3742baa2d0d9")),
          Board.byId(new ObjectId("2b48e10644ae3742baa2d0d9")))),
        None).store

      assert(stored.boards.get.size === 2)
    }

    it("should be possible to delete a project") {
      Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9")).delete

      intercept[IllegalArgumentException] {
        Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9")).delete
      }
    }

    it("should detect mid air collistion") {
      val loaded = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      val loaded2 = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.name = "new name"
      loaded.store
      
      intercept[MidAirCollisionException] {
    	  loaded2.store
      }
      
    }
    
    it("should be possible to update the name of the project") {
      val loaded = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.name = "new name"
      loaded.store
      val changed = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.name === "new name")
    }

    it("should be possible to update the board list of the project") {
      val loaded = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.boards = Some(List(
          Board.byId(new ObjectId("2b48e10644ae3742baa2d0d9")),
          Board.byId(new ObjectId("1b48e10644ae3742baa2d0d9"))
      ))
      loaded.store
      val changed = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.boards.get.size === 2)
    }
    
    it("should be possible to update the task list of the project") {
      val loaded = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.tasks = Some(List(
          Task.byId(new ObjectId("4f48e10644ae3742baa2d0a9")),
          Task.byId(new ObjectId("5f48e10644ae3742baa2d0a9"))
      ))
      
      loaded.store
      val changed = Project.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.tasks.get.size === 2)
    }

  }
}