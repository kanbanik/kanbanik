package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import org.junit.runner.RunWith
import org.mockito.Mockito.when
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.BeforeAndAfter
import org.scalatest.Spec
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.dto.WorkflowitemDto

@RunWith(classOf[JUnitRunner])
class BoardBuilderTest extends Spec with MockitoSugar {

  describe("BoardBuilder should be able to build DTOs from entity objects and vice versa") {

//    it("should handle board without workflow") {
//      val board = mock[Board]
//
//      when(board.name).thenReturn("boardName")
//      when(board.id).thenReturn(Some(new ObjectId("4f48e10644ae3742baa2d0a9")))
//      when(board.workflowitems).thenReturn(None)
//
//      val builder = new BoardBuilder
//      val dto = builder.buildDto(board)
//      assert(dto.getName() === "boardName")
//      assert(dto.getId() === "4f48e10644ae3742baa2d0a9")
//      assert(dto.getRootWorkflowitem() === null)
//
//    }
//
//    it("should find the correct workflowitem") {
//      val item1 = mockWorkflowitem("1f48e10644ae3742baa2d0a9");
//      val item2 = mockWorkflowitem("2f48e10644ae3742baa2d0a9");
//      val item3 = mockWorkflowitem("3f48e10644ae3742baa2d0a9");
//
//      when(item1.nextItem).thenReturn(Some(item2))
//      when(item2.nextItem).thenReturn(Some(item3))
//      when(item3.nextItem).thenReturn(None)
//
//      val board = mock[Board]
//      when(board.id).thenReturn(Some(new ObjectId("1f48e10644ae3742baa2d0a9")))
//      
//      val builder = new TestedBuilder
//
//      assert(builder.findRootWorkflowitem(board, Some(List(item1, item2, item3))).getId() === "1f48e10644ae3742baa2d0a9")
//      assert(builder.findRootWorkflowitem(board, Some(List(item3, item2, item1))).getId() === "1f48e10644ae3742baa2d0a9")
//      assert(builder.findRootWorkflowitem(board, Some(List(item2, item3, item1))).getId() === "1f48e10644ae3742baa2d0a9")
//
//    }
//
//    def mockWorkflowitem(id: String) = {
//      val item = mock[Workflowitem]
//      when(item.id).thenReturn(Some(new ObjectId(id)))
//      item
//    }
  }
}

class TestedBuilder extends BoardBuilder {
  override def workflowitemBuilder = new SimpleWorkflowitemBuilder

  class SimpleWorkflowitemBuilder extends WorkflowitemBuilder {
    override def buildDto(workflowitem: Workflowitem): WorkflowitemDto = {
      val dto = new WorkflowitemDto
      dto.setId(workflowitem.id.get.toString())
      dto
    }
  }
}