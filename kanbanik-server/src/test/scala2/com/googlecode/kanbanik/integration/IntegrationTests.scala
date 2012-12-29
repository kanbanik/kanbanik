package com.googlecode.kanbanik.integration

import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.commands.SaveBoardCommand
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.dto.BoardWithProjectsDto
import com.googlecode.kanbanik.commands.SaveProjectCommand
import com.googlecode.kanbanik.commands.AddProjectsToBoardCommand
import com.googlecode.kanbanik.dto.WorkflowDto
import org.scalatest.BeforeAndAfter
import com.googlecode.kanbanik.model.DbCleaner
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.WorkflowitemDto
import com.googlecode.kanbanik.dto.ItemType
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.WorkflowitemTestManipulation
import com.googlecode.kanbanik.commands.EditWorkflowCommand
import com.googlecode.kanbanik.commands.GetAllBoardsCommand
import com.googlecode.kanbanik.dto.shell.VoidParams

/**
 * This are tests which expects working DB and are trying to simmulate some basic use
 * cases of the users. They are really calling commands and so on.
 */
@RunWith(classOf[JUnitRunner])
class IntegrationTests extends FlatSpec with BeforeAndAfter with WorkflowitemTestManipulation {
  "Kanbanik" should "be able to create a new setup from scratch" in {
    val board = new BoardDto()
    board.setName("board1")
    board.setWorkflow(new WorkflowDto())
    val storedBoard = new SaveBoardCommand().execute(new SimpleParams(board))

    val project = new ProjectDto()
    project.setName("project1")
    val storedProject = new SaveProjectCommand().execute(new SimpleParams(project))

    val boardWithProjects = new BoardWithProjectsDto()
    boardWithProjects.setBoard(storedBoard.getPayload().getPayload())
    boardWithProjects.addProject(storedProject.getPayload().getPayload())
    val storedBoardWithProjects = new AddProjectsToBoardCommand().execute(new SimpleParams(boardWithProjects)).getPayload().getPayload()

    val workflow = storedBoardWithProjects.getBoard().getWorkflow()
    val item1 = itemDtoWithName("item1", storedBoardWithProjects.getBoard().getWorkflow())
    val item2 = itemDtoWithName("item2", storedBoardWithProjects.getBoard().getWorkflow())
    val item3 = itemDtoWithName("item3", storedBoardWithProjects.getBoard().getWorkflow())
    
    editWorkflow(item1, null, workflow)
    editWorkflow(item2, null, workflow)
    editWorkflow(item3, null, workflow)
    
    assert(loadAllBoards.map(dto => dto.getBoard.getName()) === List("board1"))
    assert(loadAllBoards.head.getProjectsOnBoard().get(0).getName() === "project1")

    assert(asWorkflowList(loadAllBoards.head.getBoard().getWorkflow()).map(_.getName()) === List("item1", "item2", "item3"))
    
    editWorkflow(item3, item1, workflow)
    
    assert(asWorkflowList(loadAllBoards.head.getBoard().getWorkflow()).map(_.getName()) === List("item3", "item1", "item2"))
  }
  
  def loadAllBoards() = {
	  new GetAllBoardsCommand().execute(new VoidParams).getPayload().getList().toArray().toList.asInstanceOf[List[BoardWithProjectsDto]]    
  }
  
  def asWorkflowList(workflow: WorkflowDto) = {
    workflow.getWorkflowitems().toArray().toList.asInstanceOf[List[WorkflowitemDto]]
  }
  
  def editWorkflow(item: WorkflowitemDto, nextItem: WorkflowitemDto, workflow: WorkflowDto) {
    val editWorkflowParams = new EditWorkflowParams(
    		item,
    		nextItem,
    		workflow
    )
    
    new EditWorkflowCommand().execute(editWorkflowParams)
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}