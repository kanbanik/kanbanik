package com.googlecode.kanbanik
import org.bson.types.ObjectId

class TaskScalaTest extends BaseIntegrationTest {
  describe("Task should support all the basic CRUD operations") {

    it("should be possible to retrieve entity using ID") {
      val loaded = TaskScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "task name")
      assert(loaded.description === "task description")
      assert(loaded.classOfService === 1)
      assert(loaded.workflowitem.id.get === new ObjectId("4f48e10644ae3742baa2d0a9"))
    }

    it("should be possible to store a new entity") {
      val stored = new TaskScala(
        None,
        "other name",
        "other description",
        2,
        WorkflowitemScala.byId(new ObjectId("4f48e10644ae3742baa2d0a9"))).store

      assert(stored.name === "other name")
      assert(stored.description === "other description")
    }

    it("should be possible to update the content of the entity") {
      val loaded = TaskScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.name = "changed name"
      loaded.description = "changed description"
      loaded.classOfService = 3
      loaded.store

      val changed = TaskScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.name === "changed name")
      assert(changed.description === "changed description")
      assert(changed.classOfService === 3)
    }

    it("should be possible to delete a task") {
      val task = TaskScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      task.delete
      intercept[IllegalArgumentException] {
        TaskScala.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      }
    }

    it("should fail when triing to create with a non existing workflow") {
      intercept[IllegalArgumentException] {
        new TaskScala(
          None,
          "other name",
          "other description",
          2,
          new WorkflowitemScala(None, "", 1, None, None, null)).store
      }
    }
  }
}