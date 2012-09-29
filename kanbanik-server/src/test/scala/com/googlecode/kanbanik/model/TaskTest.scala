package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import org.junit.runner.RunWith

class TaskTest extends BaseIntegrationTest {
  describe("Task should support all the basic CRUD operations") {

    it("should be possible to retrieve entity using ID") {
      val loaded = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(loaded.name === "task name")
      assert(loaded.description === "task description")
      assert(loaded.classOfService === 1)
      assert(loaded.workflowitem.id.get === new ObjectId("4f48e10644ae3742baa2d0a9"))
    }

    it("should be possible to store a new entity") {
      val stored = new Task(
        None,
        "other name",
        "other description",
        2,
        "ticket id",
        1,
        Workflowitem.byId(new ObjectId("4f48e10644ae3742baa2d0a9"))).store

      assert(stored.name === "other name")
      assert(stored.description === "other description")
      assert(stored.ticketId === "ticket id")
    }

    it("should be possible to update the content of the entity") {
      val loaded = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      loaded.name = "changed name"
      loaded.description = "changed description"
      loaded.classOfService = 3
      loaded.store

      val changed = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      assert(changed.name === "changed name")
      assert(changed.description === "changed description")
      assert(changed.classOfService === 3)
    }

    it("should be possible to delete a task") {
      val task = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      task.delete
      intercept[IllegalArgumentException] {
        Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      }
    }
    
    it("should throw exception on mid-air collision") {
      val task1 = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      val task2 = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      
      task1.store
      intercept[MidAirCollisionException] {
        task2.store
      }
    }
    
    it("should fail when deleteing a modified task") {
      val task = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      val taskToModify = Task.byId(new ObjectId("1a48e10644ae3742baa2d0d9"))
      
      task.store
      intercept[MidAirCollisionException] {
    	  taskToModify.delete
      }
    }
    
    it("should fail when triing to create with a non existing workflow") {
      intercept[IllegalArgumentException] {
        new Task(
          None,
          "other name",
          "other description",
          2,
          "",
          1,
          new Workflowitem(None, "", 1, "H", None, None, null)).store
      }
    }
  }
}