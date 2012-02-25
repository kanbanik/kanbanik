package com.googlecode.kanbanik

class WorkflowitemScalaTest extends BaseIntegrationTest {
  describe("Workflowitem should be able to do all the CRUD operations") {

    it("should be able to find what it stored") {
      val stored = new WorkflowitemScala(None, "name1", 1, None, None).store
      val loaded = WorkflowitemScala.byId(stored.id.getOrElse(notSet))
      assert(stored.name === loaded.name)
      assert(stored.wipLimit === loaded.wipLimit)
      assert(stored.id === loaded.id)

    }

    it("should fail when incorrect id specified") {
      intercept[IllegalArgumentException] {
        WorkflowitemScala.byId("not existing")
      }
    }

    it("should be able to store subworkflows") {
      val stored = new WorkflowitemScala(None, "name1", 1,
        Some(List(
          new WorkflowitemScala(None, "inner1", 1, None, None),
          new WorkflowitemScala(None, "inner2", 1, None, None))), None).store

      val children = WorkflowitemScala.byId(stored.id.getOrElse(notSet)).children.getOrElse(notSet)
      assert(children.size === 2)
    }

    it("should be able to store more deep subworkflows") {
      val stored = new WorkflowitemScala(None, "name1", 1,
        Some(List(
          new WorkflowitemScala(None, "inner1", 1, None, None),
          new WorkflowitemScala(None, "inner2", 1,
            Some(List(
              new WorkflowitemScala(None, "inner21", 1,
                Some(List(
                  new WorkflowitemScala(None, "inner211", 1, None, None),
                  new WorkflowitemScala(None, "inner2112", 1, None, None))), None),
              new WorkflowitemScala(None, "inner22", 1, None, None))), None))), None).store

      val children = WorkflowitemScala.byId(stored.id.getOrElse(notSet)).children.getOrElse(notSet)
      
      assert(children.size === 2)
      assert(children(0).children.isDefined === false)
      assert(children(1).children.getOrElse(notSet).size === 2)
      
      assert(children(1).children.getOrElse(notSet)(0).children.getOrElse(notSet).size === 2)
      assert(children(1).children.getOrElse(notSet)(1).children.isDefined === false)
    }

    def notSet = {
      throw new IllegalStateException("Required field is not set")
    }
  }
}