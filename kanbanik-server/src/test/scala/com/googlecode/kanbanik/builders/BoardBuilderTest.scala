package com.googlecode.kanbanik.builders
import org.bson.types.ObjectId
import org.junit.runner.RunWith
import org.mockito.Mockito.when
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.scalatest.BeforeAndAfter
import org.scalatest.Spec
import com.googlecode.kanbanik.model.BoardScala
import com.googlecode.kanbanik.model.WorkflowitemScala

@RunWith(classOf[JUnitRunner])
class BoardBuilderTest extends Spec with MockitoSugar {

  describe("BoardBuilder should be able to build DTOs from entity objects and vice versa") {

    it("should handle board without workflow") {
      val board = mock[BoardScala]

      when(board.name).thenReturn("boardName")
      when(board.id).thenReturn(Some(new ObjectId("4f48e10644ae3742baa2d0a9")))
      when(board.workflowitems).thenReturn(None)

      val builder = new BoardBuilder
      val dto = builder.buildDto(board)
      assert(dto.getName() === "boardName")
      assert(dto.getId() === "4f48e10644ae3742baa2d0a9")
      assert(dto.getRootWorkflowitem() === null)

    }

  }
}