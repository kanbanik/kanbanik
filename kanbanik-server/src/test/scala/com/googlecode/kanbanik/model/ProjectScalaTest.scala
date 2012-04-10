package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import org.junit.runner.RunWith

class ProjectScalaTest extends BaseIntegrationTest {
  describe("Project should be able to do all the basic CRUD operations") {

    it("should be possible to retrive a project without task and board") {
      val loaded = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "project name")
      assert(loaded.boards.isDefined === false)
      assert(loaded.tasks.isDefined === false)
    }

    it("should be possible to retrive a project without tasks and with boards") {
      val loaded = ProjectScala.byId(new ObjectId("2a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "project name")
      assert(loaded.boards.get.size === 2)
      assert(loaded.tasks.isDefined === false)
    }

    it("should be possible to retrive a project with tasks and with boards") {
      val loaded = ProjectScala.byId(new ObjectId("3a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "project name")
      assert(loaded.boards.get.size === 2)
      assert(loaded.tasks.get.size === 2)
    }

    it("should be possible to create a new entity without boards and without tasks") {
      val stored = new ProjectScala(None,
        "some name",
        None,
        None).store

      assert(stored.name === "some name")
    }

    it("should be possible to create a new entity without boards and with tasks") {
      val stored = new ProjectScala(None,
        "some name",
        None,
        Some(List(
          TaskScala.byId(new ObjectId("4f48e10644ae3742baa2d0a9")),
          TaskScala.byId(new ObjectId("5f48e10644ae3742baa2d0a9"))))).store

      assert(stored.tasks.get.size === 2)
    }

    it("should be possible to create a new entity with boards and without tasks") {
      val stored = new ProjectScala(None,
        "some name",
        Some(List(
          BoardScala.byId(new ObjectId("1b48e10644ae3742baa2d0d9")),
          BoardScala.byId(new ObjectId("2b48e10644ae3742baa2d0d9")))),
        None).store

      assert(stored.boards.get.size === 2)
    }

    it("should be possible to delete a project") {
      ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9")).delete

      intercept[IllegalArgumentException] {
        ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9")).delete
      }
    }

    it("should be possible to update the name of the project") {
      val loaded = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.name = "new name"
      loaded.store
      val changed = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.name === "new name")
    }

    it("should be possible to update the board list of the project") {
      val loaded = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.boards = Some(List(
          BoardScala.byId(new ObjectId("2b48e10644ae3742baa2d0d9")),
          BoardScala.byId(new ObjectId("1b48e10644ae3742baa2d0d9"))
      ))
      loaded.store
      val changed = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.boards.get.size === 2)
    }
    
    it("should be possible to update the task list of the project") {
      val loaded = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.tasks = Some(List(
          TaskScala.byId(new ObjectId("4f48e10644ae3742baa2d0a9")),
          TaskScala.byId(new ObjectId("5f48e10644ae3742baa2d0a9"))
      ))
      
      loaded.store
      val changed = ProjectScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.tasks.get.size === 2)
    }

  }
}