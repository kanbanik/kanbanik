package com.googlecode.kanbanik.model

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.bson.types.ObjectId
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class WorkflowTest extends BaseWorkflowManipulatingTest with BeforeAndAfter {

  "removeItem() flat" should "be able to remove the only item" in {
    val item = mkWorkflowItem(new ObjectId(), Workflow())
    val workflow = Workflow(List(
      item))

    val res = workflow.removeItem(item)
    assert(res.workflowitems === List())

  }

  val item1 = mkWorkflowItem(new ObjectId(), Workflow())
  val item2 = mkWorkflowItem(new ObjectId(), Workflow())
  val item3 = mkWorkflowItem(new ObjectId(), Workflow())

  val someWorkflow = Workflow(new ObjectId(), List(
    item1, item2, item3))

  it should "be able to remove the first item" in {
    val res = someWorkflow.removeItem(item1)
    assert(res.workflowitems === List(item2, item3))
  }

  it should "be able to remove the middle item" in {
    val res = someWorkflow.removeItem(item2)
    assert(res.workflowitems === List(item1, item3))
  }

  val item1_1 = mkWorkflowItem(new ObjectId(), Workflow())
  val item2_1 = mkWorkflowItem(new ObjectId(), someWorkflow)
  val item3_1 = mkWorkflowItem(new ObjectId(), Workflow())

  val workflow_1 = Workflow(new ObjectId(), List(
    item1_1, item2_1, item3_1))

  "removeItem() nested" should "be able to remove item in first level" in {
    val resFirst = someWorkflow.removeItem(item1)
    assert(resFirst.workflowitems === List(item2, item3))

    val resLast = someWorkflow.removeItem(item3)
    assert(resLast.workflowitems === List(item1, item2))
  }

  "removeItem() nested" should "be able to remove item in second level" in {
    val resFirst = workflow_1.removeItem(item1)
    assert(resFirst.workflowitems(1).nestedWorkflow.workflowitems === List(item2, item3))

    val resLast = workflow_1.removeItem(item3)
    assert(resLast.workflowitems(1).nestedWorkflow.workflowitems === List(item1, item2))
  }

  "addItem() first leve" should "be able to add an item at the beginning" in {
    val res = someWorkflow.addItem(item1_1, Some(item1))

    assert(res.workflowitems === List(item1_1, item1, item2, item3))
  }

  it should "be able to add an item to the end" in {
    val res = someWorkflow.addItem(item1_1, None)

    assert(res.workflowitems === List(item1, item2, item3, item1_1))
  }

  it should "be able to add an item in the middle" in {
    val res = someWorkflow.addItem(item1_1, Some(item2))

    assert(res.workflowitems === List(item1, item1_1, item2, item3))
  }

  it should "be able to add an item to an empty list" in {
    val res = Workflow().addItem(item1, None)

    assert(res.workflowitems === List(item1))
  }

  "addItem() second level" should "be able to add at the beginning" in {
    val itemToAdd = mkWorkflowItem(new ObjectId(), Workflow())
    val res = workflow_1.addItem(itemToAdd, Some(item1), someWorkflow)
    
    assert(res.workflowitems(1).nestedWorkflow.workflowitems === List(itemToAdd, item1, item2, item3))
  }
  
  it should "be able to add at the end" in {
    val itemToAdd = mkWorkflowItem(new ObjectId(), Workflow())
    val res = workflow_1.addItem(itemToAdd, None, someWorkflow)
    
    assert(res.workflowitems(1).nestedWorkflow.workflowitems === List(item1, item2, item3, itemToAdd))
  }
  
  it should "be able to add at the middle" in {
    val itemToAdd = mkWorkflowItem(new ObjectId(), Workflow())
    val res = workflow_1.addItem(itemToAdd, Some(item2), someWorkflow)
    
    assert(res.workflowitems(1).nestedWorkflow.workflowitems === List(item1, itemToAdd, item2, item3))
  }
}