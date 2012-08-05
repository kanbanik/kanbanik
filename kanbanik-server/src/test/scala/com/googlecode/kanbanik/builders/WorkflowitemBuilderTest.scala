package com.googlecode.kanbanik.builders
import org.junit.runner.RunWith
import org.mockito.Mockito.when
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.Spec
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.ItemType
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.model.BoardScala

@RunWith(classOf[JUnitRunner])
class WorkflowitemBuilderTest extends Spec with MockitoSugar {

  describe("WorkflowitemBuilder should be able to build DTOs from entity objects and vice versa") {

    it("should work with no children and no siblings") {
      val workflowitem = mockWorkflowitem("4f48e10644ae3742baa2d0a9", "someName", 18, None, None)

      val builder = new TestedWorkflowitemBuilder
      val res = builder.buildDto(workflowitem)
      assertDtoIs(res, "4f48e10644ae3742baa2d0a9", "someName", 18)
    }

    it("should work with no children and one sibling") {
      val workflowitem1 = mockWorkflowitem("2f48e10644ae3742baa2d0a9", "2someName", 2, None, None)
      val workflowitem2 = mockWorkflowitem("1f48e10644ae3742baa2d0a9", "1someName", 1, Some(workflowitem1), None)

      val builder = new TestedWorkflowitemBuilder
      val res = builder.buildDto(workflowitem2)

      assertDtoIs(res, "1f48e10644ae3742baa2d0a9", "1someName", 1);
      assertDtoIs(res.getNextItem(), "2f48e10644ae3742baa2d0a9", "2someName", 2);
    }

    it("should work with no children and two siblings") {
      val workflowitem3 = mockWorkflowitem("3f48e10644ae3742baa2d0a9", "3someName", 3, None, None)
      val workflowitem2 = mockWorkflowitem("2f48e10644ae3742baa2d0a9", "2someName", 2, Some(workflowitem3), None)
      val workflowitem1 = mockWorkflowitem("1f48e10644ae3742baa2d0a9", "1someName", 1, Some(workflowitem2), None)

      val builder = new TestedWorkflowitemBuilder
      val res = builder.buildDto(workflowitem1)

      assertDtoIs(res, "1f48e10644ae3742baa2d0a9", "1someName", 1);
      assertDtoIs(res.getNextItem(), "2f48e10644ae3742baa2d0a9", "2someName", 2);
      assertDtoIs(res.getNextItem().getNextItem(), "3f48e10644ae3742baa2d0a9", "3someName", 3);
    }

    it("should work with one child and no siblings") {
      val workflowitem11 = mockWorkflowitem("1148e10644ae3742baa2d0a9", "11someName", 11, None, None)
      val workflowitem1 = mockWorkflowitem("1f48e10644ae3742baa2d0a9", "1someName", 1, None, Some(workflowitem11))

      val builder = new TestedWorkflowitemBuilder
      val res = builder.buildDto(workflowitem1)

      assertDtoIs(res, "1f48e10644ae3742baa2d0a9", "1someName", 1);
      assertDtoIs(res.getChild(), "1148e10644ae3742baa2d0a9", "11someName", 11);
    }

    it("should work with one child and one siblings") {
      val workflowitem11 = mockWorkflowitem("1148e10644ae3742baa2d0a9", "11someName", 11, None, None)
      val workflowitem2 = mockWorkflowitem("2f48e10644ae3742baa2d0a9", "2someName", 2, None, None)
      val workflowitem1 = mockWorkflowitem("1f48e10644ae3742baa2d0a9", "1someName", 1, Some(workflowitem2), Some(workflowitem11))

      val builder = new TestedWorkflowitemBuilder
      val res = builder.buildDto(workflowitem1)

      assertDtoIs(res, "1f48e10644ae3742baa2d0a9", "1someName", 1);
      assertDtoIs(res.getChild(), "1148e10644ae3742baa2d0a9", "11someName", 11);
      assertDtoIs(res.getNextItem(), "2f48e10644ae3742baa2d0a9", "2someName", 2);
    }

    it("should work when the child has siblings") {
      val workflowitem12 = mockWorkflowitem("1248e10644ae3742baa2d0a9", "12someName", 12, None, None)
      val workflowitem11 = mockWorkflowitem("1148e10644ae3742baa2d0a9", "11someName", 11, Some(workflowitem12), None)
      val workflowitem1 = mockWorkflowitem("1f48e10644ae3742baa2d0a9", "1someName", 1, None, Some(workflowitem11))

      val builder = new TestedWorkflowitemBuilder
      val res = builder.buildDto(workflowitem1)

      assertDtoIs(res, "1f48e10644ae3742baa2d0a9", "1someName", 1);
      assertDtoIs(res.getChild(), "1148e10644ae3742baa2d0a9", "11someName", 11);
      assertDtoIs(res.getChild().getNextItem(), "1248e10644ae3742baa2d0a9", "12someName", 12);
    }

  }

  def mockWorkflowitem(id: String, name: String, wip: Int, nextItem: Option[WorkflowitemScala], child: Option[WorkflowitemScala]) = {
    val item = mock[WorkflowitemScala]
    when(item.id).thenReturn(Some(new ObjectId(id)))
    when(item.name).thenReturn(name)
    when(item.wipLimit).thenReturn(wip)
    when(item.child).thenReturn(child)
    when(item.nextItem).thenReturn(nextItem)
    when(item.itemType).thenReturn("H")
    item
  }

  def assertDtoIs(dto: WorkflowitemDto, id: String, name: String, wip: Int) {
    (dto.getId() === id)
    (dto.getName() === name)
    (dto.getWipLimit() === wip)
    (dto.getItemType === ItemType.HORIZONTAL)
  }

  class TestedWorkflowitemBuilder extends WorkflowitemBuilder {
    
    override def boardBuilder = new SimpleBoardBuilder
    
    class SimpleBoardBuilder extends BoardBuilder {
      override def buildShallowDto(board: BoardScala): BoardDto = new BoardDto
    }
  }
}