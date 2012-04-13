package com.googlecode.kanbanik.commands
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.builders.BoardBuilder
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.dto.BoardDto
import org.mockito.Mockito.when
import com.googlecode.kanbanik.dto.ProjectDto

@RunWith(classOf[JUnitRunner])
class GetAllBoardsCommandTest extends Spec with MockitoSugar {

  class TestingGetAllBoardsCommand(
    val boardBuilder: BoardBuilder,
    val projectBuilder: ProjectBuilder,
    val allProjectsMocked: List[ProjectScala]) extends GetAllBoardsCommand {

    override def getBoardBuilder = boardBuilder
    override def getProjectBuilder = projectBuilder
    override def allProjects = allProjectsMocked
  }

  val projectBuilder = mock[ProjectBuilder]

  describe("It should correctly assign the list of projects for the specific board") {
    it("does nothing when no projects are assigned") {
      val list = new ListDto[BoardWithProjectsDto]()
      new TestingGetAllBoardsCommand(null, null, List()).buildProjectsForBoard(list)
      assert(list.getList().size === 0)
    }

    it("assignes nothing if there is one project but not assigned to the board") {
      val list = new ListDto[BoardWithProjectsDto]()
      val project = mock[ProjectScala]
      val projectBuilder = mock[ProjectBuilder]

      new TestingGetAllBoardsCommand(null, projectBuilder, List(project)).buildProjectsForBoard(list)
      assert(list.getList().size === 0)
    }

    it("assignes the project if there is one project assigned to one board") {
      val list = new ListDto[BoardWithProjectsDto]()
      val project = projectWithBoard("id1")
      createBoard(list, "id1")

      new TestingGetAllBoardsCommand(null, projectBuilder, List(project)).buildProjectsForBoard(list)
      assert(list.getList().get(0).getProjectsOnBoard().size() === 1)
    }

    it("should work for one board with two projects") {
      val list = new ListDto[BoardWithProjectsDto]()
      val project1 = projectWithBoard("id1")
      val project2 = projectWithBoard("id1")
      createBoard(list, "id1")

      new TestingGetAllBoardsCommand(null, projectBuilder, List(project1, project2)).buildProjectsForBoard(list)
      assert(list.getList().get(0).getProjectsOnBoard().size() === 2)
    }
    
    
    it("should work for one board with no projects but two projects on other board") {
      val list = new ListDto[BoardWithProjectsDto]()
      val project1 = projectWithBoard("id1")
      val project2 = projectWithBoard("id1")
      createBoard(list, "id2")

      new TestingGetAllBoardsCommand(null, projectBuilder, List(project1, project2)).buildProjectsForBoard(list)
      assert(list.getList().get(0).getProjectsOnBoard().size() === 0)
    }

    it("should work for two boards with 2 projects on each") {
      val list = new ListDto[BoardWithProjectsDto]()
      val project1 = projectWithBoard("id1")
      val project2 = projectWithBoard("id1")
      val project3 = projectWithBoard("id2")
      val project4 = projectWithBoard("id2")
      createBoard(list, "id1")
      createBoard(list, "id2")

      new TestingGetAllBoardsCommand(null, projectBuilder, List(project1, project2, project3, project4)).buildProjectsForBoard(list)
      assert(list.getList().get(0).getProjectsOnBoard().size() === 2)
      assert(list.getList().get(1).getProjectsOnBoard().size() === 2)
    }
    
  }

  private def projectWithBoard(boardIds: String*) = {
    val project = mock[ProjectScala]

    val projectDto = new ProjectDto()
    for (boardId <- boardIds) {
      val boardDto = new BoardDto()
      boardDto.setId(boardId)
      projectDto.addBoard(boardDto)

    }
    when(projectBuilder.buildDto(project)).thenReturn(projectDto)

    project
  }

  private def createBoard(boardWithProjectsList: ListDto[BoardWithProjectsDto], id: String) {
    val board = new BoardDto()
    board.setId(id)
    boardWithProjectsList.addItem(new BoardWithProjectsDto(board))
  }
}