package com.googlecode.kanbanik.model

import org.bson.types.ObjectId

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

class Workflow(
  val id: Option[ObjectId],
  val workflowitems: List[Workflowitem],
  val _board: Option[Board]) extends HasMongoConnection {

  def addItem(item: Workflowitem, nextItem: Option[Workflowitem], destWorkflow: Workflow): Workflow = {

    def addInThisWorkflow = {
      if (!nextItem.isDefined) {
        new Workflow(id, workflowitems ++ List(item), _board)
      } else {
        val indexOfNext = workflowitems indexOf (nextItem.get)
        val added = workflowitems.take(indexOfNext) ++ List(item) ++ workflowitems.drop(indexOfNext)
        new Workflow(id, added, _board)
      }
    }

    def addInInnerWorkflow(inItems: List[Workflowitem]): List[Workflowitem] = {
      inItems match {
        case Nil => Nil
        case x :: xs => {
          x.withWorkflow(x.nestedWorkflow.addItem(item, nextItem, destWorkflow)) :: addInInnerWorkflow(xs)
        }
      }
    }

    if (destWorkflow == this) {
      addInThisWorkflow
    } else {
      new Workflow(id, addInInnerWorkflow(workflowitems), _board)
    }

  }
  
  def addItem(item: Workflowitem, nextItem: Option[Workflowitem]): Workflow = addItem(item, nextItem, this)
  
  def replaceItem(item: Workflowitem): Workflow = {
	modifyWorkflow(item)((x, xs) => item :: xs)
  }
  
  def removeItem(item: Workflowitem): Workflow = {
    modifyWorkflow(item)((x, xs) => xs)
  }

  def containsItem(item: Workflowitem): Boolean = {
   findItem(item).isDefined
  }
  
  def findItem(item: Workflowitem): Option[Workflowitem] = {
    // boooo, mutability :(
    var found: Option[Workflowitem] = None
    modifyWorkflow(item)((x, xs) => {found = Some(x); x :: xs})
    found
  }
  
  private def modifyWorkflow(item: Workflowitem)(found: (Workflowitem, List[Workflowitem]) => List[Workflowitem]): Workflow = {
    def modifyWorkflow(items: List[Workflowitem]): List[Workflowitem] = {
      items match {
        case Nil => Nil
        case x :: xs =>
          if (x == item) found(x, xs)
          else
            x.withWorkflow(new Workflow(
              x.nestedWorkflow.id,
              modifyWorkflow(x.nestedWorkflow.workflowitems), _board)) :: modifyWorkflow(xs)
      }
    }

    new Workflow(id, modifyWorkflow(workflowitems), _board)
  }
  
  def board = _board.getOrElse(loadBoard)

  def loadBoard = {
    
    def containsThisWorkflowInItems(workflowitems: List[Workflowitem]): Boolean = {
        workflowitems match {
          case Nil => false
          case x :: xs => 
            containsThisWorkflow(x.nestedWorkflow) ||  containsThisWorkflowInItems(xs)
        }
    }
    
    def containsThisWorkflow(workflow: Workflow): Boolean = {
      if (workflow == this) {
        true
      } else {
        containsThisWorkflowInItems(workflow.workflowitems)
      }
    }
    
    // quite a heavy operation
    val board = Board.all.find(board => containsThisWorkflow(board.workflow))
    
	board.getOrElse(throw new IllegalStateException("The workflow with id: '" + id + "' does not exist!"))
    
  }
  
  def throwEx = {
	  throw new IllegalStateException("The workflow : " + id + "has to have a board")
  }

  def withWorkflowitems(workflowitems: List[Workflowitem]) =
    new Workflow(id, workflowitems, _board)

  def withBoard(board: Option[Board]) =
    new Workflow(id, workflowitems, board)

  def asDbObject(): DBObject = {
    MongoDBObject(
      Workflow.Fields.id.toString() -> id.getOrElse(new ObjectId),
      Workflow.Fields.workflowitems.toString() -> workflowitems.map(_.asDbObject))
  }

  def canEqual(other: Any) = {
    other.isInstanceOf[com.googlecode.kanbanik.model.Workflow]
  }

  override def equals(other: Any) = {
    other match {
      case that: Workflow => that.canEqual(Workflow.this) && id == that.id
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime + id.hashCode
  }

}

object Workflow extends HasMongoConnection {

  object Fields extends DocumentField {
    val workflowitems = Value("workflowitems")
  }

  def apply() = new Workflow(Some(new ObjectId()), List(), None)
  def apply(items: List[Workflowitem]) = new Workflow(Some(new ObjectId()), items, None)
  def apply(id: ObjectId, items: List[Workflowitem]) = new Workflow(Some(id), items, None)

  def asEntity(dbObject: DBObject): Workflow = {
    asEntity(dbObject, None)
  }

  def asEntity(dbObject: DBObject, board: Option[Board]): Workflow = {

    val workflow = new Workflow(
      Some(dbObject.get(Fields.id.toString()).asInstanceOf[ObjectId]),
      {
        val loadedWorkflowitems = dbObject.get(Fields.workflowitems.toString())
        if (loadedWorkflowitems.isInstanceOf[BasicDBList]) {
          val list = dbObject.get(Fields.workflowitems.toString()).asInstanceOf[BasicDBList].toArray().toList.asInstanceOf[List[DBObject]]
          list.map(Workflowitem.asEntity(_))
        } else {
          List()
        }
      },
      board)

    // it is filled in the second phase to avoid circular dependency 
    workflow.withWorkflowitems(workflow.workflowitems.map(_.withParentWorkflow(workflow)))

  }

}