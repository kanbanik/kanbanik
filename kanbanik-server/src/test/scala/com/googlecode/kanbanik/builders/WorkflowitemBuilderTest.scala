package com.googlecode.kanbanik.builders
import org.junit.runner.RunWith
import org.mockito.Mockito.when
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.Spec
import com.googlecode.kanbanik.model.WorkflowitemScala
import org.bson.types.ObjectId
import com.googlecode.kanbanik.dto.WorkflowitemDto

@RunWith(classOf[JUnitRunner])
class WorkflowitemBuilderTest extends Spec with MockitoSugar {

  describe("WorkflowitemBuilder should be able to build DTOs from entity objects and vice versa") {

    it("should fill all properties with no children") {
      val workflowitem = mockWorkflowitem("4f48e10644ae3742baa2d0a9", "someName", 18, None)

      val builder = new WorkflowitemBuilder
      val res = builder.buildDto(workflowitem)
      assert(res.getId() === "4f48e10644ae3742baa2d0a9")
      assert(res.getName() === "someName")
      assert(res.getWipLimit() === 18)
      assert(res.getChildren().size() === 0)

      assertDtoIs(res, "4f48e10644ae3742baa2d0a9", "someName", 18, 0)
    }

    it("should fill all properties with one child") {
      val innerWorkflowitem = mockWorkflowitem("5f48e10644ae3742baa2d0a9", "inner someName", 5, None)
      val workflowitem = mockWorkflowitem("4f48e10644ae3742baa2d0a9", "someName", 18, Some(List(innerWorkflowitem)))

      val builder = new WorkflowitemBuilder
      val res = builder.buildDto(workflowitem)

      assertDtoIs(res, "4f48e10644ae3742baa2d0a9", "someName", 18, 1)
      assertDtoIs(res.getChildren().get(0), "5f48e10644ae3742baa2d0a9", "inner someName", 5, 0)

    }

    it("should fill all properties with more children") {
      val innerInnerWorkflowitem = mockWorkflowitem("6f48e10644ae3742baa2d0a9", "inner inner someName", 7, None)
      val innerWorkflowitem = mockWorkflowitem("5f48e10644ae3742baa2d0a9", "inner someName", 5, Some(List(innerInnerWorkflowitem)))
      val workflowitem = mockWorkflowitem("4f48e10644ae3742baa2d0a9", "someName", 18, Some(List(innerWorkflowitem)))

      val builder = new WorkflowitemBuilder
      val res = builder.buildDto(workflowitem)

      assertDtoIs(res, "4f48e10644ae3742baa2d0a9", "someName", 18, 1)
      assertDtoIs(res.getChildren().get(0), "5f48e10644ae3742baa2d0a9", "inner someName", 5, 1)
      assertDtoIs(res.getChildren().get(0).getChildren().get(0), "6f48e10644ae3742baa2d0a9", "inner inner someName", 7, 0)

    }
  }

  def mockWorkflowitem(id: String, name: String, wip: Int, children: Option[List[WorkflowitemScala]]) = {
    val item = mock[WorkflowitemScala]
    when(item.id).thenReturn(Some(new ObjectId(id)))
    when(item.name).thenReturn(name)
    when(item.wipLimit).thenReturn(wip)
    when(item.children).thenReturn(children)
    item
  }

  def assertDtoIs(dto: WorkflowitemDto, id: String, name: String, wip: Int, childCount: Int) {
    assert(dto.getId() === id)
    assert(dto.getName() === name)
    assert(dto.getWipLimit() === wip)
    assert(dto.getChildren().size() === childCount)
  }

}