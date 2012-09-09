package com.googlecode.kanbanik.builders
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.model.Project
import org.mockito.Mockito.when
import org.bson.types.ObjectId
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.model.Workflowitem
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.dto.TaskDto

@RunWith(classOf[JUnitRunner])
class ProjectBuilderTest extends Spec with MockitoSugar {

  describe("ProjectBuilder should be able to build DTOs from entity objects and vice versa") {

    it("should fill all properties with no boards and no tasks") {
      val project = mock[Project]
      when(project.id).thenReturn(Some(new ObjectId("4f48e10644ae3742baa2d0a9")))
      when(project.name).thenReturn("someName")
      when(project.boards).thenReturn(None)
      when(project.tasks).thenReturn(None)

      val builder = new ProjectBuilder
      val res = builder.buildDto(project)

      assert(res.getId() === "4f48e10644ae3742baa2d0a9")
      assert(res.getName() === "someName")
      assert(res.getBoards().size() === 0)
      assert(res.getTasks().size() === 0)
    }

    it("should fill all properties with boards and tasks") {
      val board = mock[Board]

      when(board.name).thenReturn("boardName")
      when(board.id).thenReturn(Some(new ObjectId("4f48e10644ae3742baa2d0a9")))
      when(board.workflowitems).thenReturn(None)

      val task = mock[Task]
      val workflowitem = mock[Workflowitem]

      when(workflowitem.id).thenReturn(Some(new ObjectId("6f48e10644ae3742baa2d0a9")))
      when(workflowitem.child).thenReturn(None)
      when(workflowitem.itemType).thenReturn("H")

      when(task.id).thenReturn(Some(new ObjectId("4f48e10644ae3742baa2d0a9")))
      when(task.workflowitem).thenReturn(workflowitem)

      val project = mock[Project]
      when(project.id).thenReturn(Some(new ObjectId("4f48e10644ae3742baa2d0a9")))
      when(project.name).thenReturn("someName")
      when(project.boards).thenReturn(Some(List(board)))
      when(project.tasks).thenReturn(Some(List(task)))

      val builder = new TestedProjectBuilder
      val res = builder.buildDto(project)

      assert(res.getId() === "4f48e10644ae3742baa2d0a9")
      assert(res.getName() === "someName")
      assert(res.getBoards().size() === 1)
      assert(res.getTasks().size() === 1)
    }
  }

  class TestedProjectBuilder extends ProjectBuilder {

    override def taskBuilder = new SimpleTaskBilder

    class SimpleTaskBilder extends TaskBuilder {
    	override def buildDto(task: Task) = new TaskDto 
    }
  }

}