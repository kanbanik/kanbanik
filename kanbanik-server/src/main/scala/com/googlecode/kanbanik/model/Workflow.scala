package com.googlecode.kanbanik.model

import org.bson.types.ObjectId

import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.BasicDBList
import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject

case class Workflow(
  id: Option[ObjectId],
  workflowitems: List[Workflowitem],
  _board: Option[Board]) extends HasMongoConnection {

  def addItem(item: Workflowitem, nextItem: Option[Workflowitem], destWorkflow: Workflow): Workflow = {

    def addInThisWorkflow() = {
      if (!nextItem.isDefined) {
        new Workflow(id, workflowitems ++ List(item), _board)
      } else {
        val indexOfNext = workflowitems indexOf nextItem.get
        val added = workflowitems.take(indexOfNext) ++ List(item) ++ workflowitems.drop(indexOfNext)
        new Workflow(id, added, _board)
      }
    }

    def addInInnerWorkflow(inItems: List[Workflowitem]): List[Workflowitem] = {
      inItems match {
        case Nil => Nil
        case x :: xs => {
          x.copy(nestedWorkflow = x.nestedWorkflow.addItem(item, nextItem, destWorkflow)) :: addInInnerWorkflow(xs)
        }
      }
    }

    if (destWorkflow == this) {
      addInThisWorkflow()
    } else {
      new Workflow(id, addInInnerWorkflow(workflowitems), _board)
    }

  }
  
  def addItem(item: Workflowitem, nextItem: Option[Workflowitem]): Workflow = addItem(item, nextItem, this)
  
  def replaceItem(item: Workflowitem): Workflow = {
    traverseForModify(item)((x, xs) => item :: xs)
  }
  
  def removeItem(item: Workflowitem): Workflow = {
    traverseForModify(item)((x, xs) => xs)
  }

  def containsItem(item: Workflowitem): Boolean = {
   findItem(item).isDefined
  }
  
  def findParentItem(nestedWorkflow: Workflow) = {
    traverseForFind(item => item != null && item.nestedWorkflow == nestedWorkflow)
  }
  
  def findItem(item: Workflowitem) = {
    traverseForFind(_ == item)
  }
  
  private def traverseForModify(item: Workflowitem)(foundAction: (Workflowitem, List[Workflowitem]) => List[Workflowitem]) = {
    traverseWorkflow[Workflowitem, Workflow](item == _)(foundAction)(createWorkflow)(createWorkflowResult)
  }
  
  private def traverseForFind(foundPredicate: Workflowitem => Boolean) = {
    traverseWorkflow[Option[Workflowitem], Option[Workflowitem]](foundPredicate)((x, xs) => List(Some(x)))(findNotFoundAction(foundPredicate))(findCreateResult)
  }

  def findCreateResult(items: List[Option[Workflowitem]]) = {
    items.find(_.isDefined).getOrElse(None)
  }
  
  def findNotFoundAction(foundPredicate: Workflowitem => Boolean)(item: Workflowitem, nested: List[Option[Workflowitem]]) = {
    nested.find(candidate => foundPredicate(candidate.orNull)).getOrElse(None)
  }
  
  def findFoundAction(item: Workflowitem, rest: List[Workflowitem]) = {
    Some(item)
  }
  
  def createWorkflow(item: Workflowitem, nested: List[Workflowitem]) = {
	  item.copy(nestedWorkflow =
			  new Workflow(
					  item.nestedWorkflow.id,
					  nested, _board)
			  )
  } 

  def createWorkflowResult(items: List[Workflowitem]) = {
    new Workflow(id, items, _board)
  }
  
  private def traverseWorkflow[T,U]
  	(matchPredicate: Workflowitem => Boolean)
  	(matchedAction: (Workflowitem, List[Workflowitem]) => List[T])
  	(notMatchedAction: (Workflowitem, List[T]) => T)
  	(makeResult: List[T] => U)
  	: U = {
    def traverseWorkflow(items: List[Workflowitem]): List[T] = {
      items match {
        case Nil => Nil
        case x :: xs => {
          if (matchPredicate(x)) {
            matchedAction(x, xs)
          } else {
            notMatchedAction(x, traverseWorkflow(x.nestedWorkflow.workflowitems)) :: traverseWorkflow(xs)
          }
        }
      }
    }

    makeResult(traverseWorkflow(workflowitems))
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
    val board = Board.all(includeTasks = false).find(board => containsThisWorkflow(board.workflow))
    
	board.getOrElse(throw new IllegalStateException("The workflow with id: '" + id + "' does not exist!"))
    
  }
  
  def throwEx = {
	  throw new IllegalStateException("The workflow : " + id + "has to have a board")
  }

  def asDbObject(): DBObject = {
    MongoDBObject(
      Workflow.Fields.id.toString -> id.getOrElse(new ObjectId),
      Workflow.Fields.workflowitems.toString -> workflowitems.map(_.asDbObject()))
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
  def apply(board: Board) = new Workflow(Some(new ObjectId()), List(), Some(board))

  def asEntity(dbObject: DBObject): Workflow = {
    asEntity(dbObject, None)
  }

  def asEntity(dbObject: DBObject, board: Option[Board]): Workflow = {

    val workflow = new Workflow(
      Some(dbObject.get(Fields.id.toString).asInstanceOf[ObjectId]),
      {
        val loadedWorkflowitems = dbObject.get(Fields.workflowitems.toString)
        if (loadedWorkflowitems.isInstanceOf[BasicDBList]) {
          val list = dbObject.get(Fields.workflowitems.toString).asInstanceOf[BasicDBList].toArray.toList.asInstanceOf[List[DBObject]]
          list.map(Workflowitem.asEntity)
        } else {
          List()
        }
      },
      board)

    // it is filled in the second phase to avoid circular dependency 
    workflow.copy(workflowitems = workflow.workflowitems.map(_.copy(_parentWorkflow = Some(workflow))))

  }

}