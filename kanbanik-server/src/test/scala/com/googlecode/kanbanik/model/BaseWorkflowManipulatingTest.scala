package com.googlecode.kanbanik.model;

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.bson.types.ObjectId
import org.scalatest.BeforeAndAfter
import com.googlecode.kanbanik.db.HasMongoConnection
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing

@RunWith(classOf[JUnitRunner])
class BaseWorkflowManipulatingTest extends FlatSpec with BeforeAndAfter with HasMongoConnection {

  val a = mkWorkflowItem(new ObjectId(), Workflow())
  val b = mkWorkflowItem(new ObjectId(), Workflow())
  val c = mkWorkflowItem(new ObjectId(), Workflow())
  val workflow = Workflow(List(a, b, c))
  val board = new Board(None, "", 1, workflow, List(), WorkfloVerticalSizing.BALANCED, 0)

  val a1 = mkWorkflowItem(new ObjectId(), Workflow())
  val b1 = mkWorkflowItem(new ObjectId(), Workflow())
  val c1 = mkWorkflowItem(new ObjectId(), Workflow())
  val workflow1 = Workflow(List(a1, b1, c1))

  val b0Id = new ObjectId()
  val b0 = mkWorkflowItem(b0Id, workflow1)
  val workflow0 = Workflow(List(a, b0, c))
  val board1 = new Board(None, "", 1, workflow0, List(), WorkfloVerticalSizing.BALANCED, 0)

  
  
  after {
	  // cleanup database
    DbCleaner.clearDb
  }
  
  def mkWorkflowItem(id: ObjectId, nestedWorkflow: Workflow) = {
    new Workflowitem(Some(id), "", -1, "", 1, nestedWorkflow, None)
  }
}
