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
    project.setName("name1")
    val storedProject = new SaveProjectCommand().execute(new SimpleParams(project))

    val boardWithProjects = new BoardWithProjectsDto()
    boardWithProjects.setBoard(storedBoard.getPayload().getPayload())
    boardWithProjects.addProject(storedProject.getPayload().getPayload())
    val storedBoardWithProjects = new AddProjectsToBoardCommand().execute(new SimpleParams(boardWithProjects)).getPayload().getPayload()

    
    val item = itemDtoWithName("item1")
    item.setParentWorkflow(storedBoardWithProjects.getBoard().getWorkflow())
    
    val editWorkflowParams = new EditWorkflowParams(
    		item,
    		null,
    		storedBoardWithProjects.getBoard().getWorkflow()
    )
    
    new EditWorkflowCommand().execute(editWorkflowParams)
    
    println()
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}