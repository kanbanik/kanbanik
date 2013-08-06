package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter
import com.googlecode.kanbanik.model.DbCleaner
import com.googlecode.kanbanik.dto.ClassOfServiceDto
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.commands.SaveBoardCommand
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.Board
import org.bson.types.ObjectId
import com.googlecode.kanbanik.commands.SaveClassOfServiceCommand
import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.model.Workflow
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.commands.DeleteClassOfServiceCommand
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing

@RunWith(classOf[JUnitRunner])
class ClassOfServiceIntegrationTest extends FlatSpec with BeforeAndAfter {

  "class of service" should "should be able to do the whole cycle" in {
    val boardDto = new BoardDto
    boardDto.setName("boardName")
    boardDto.setWorkflow(new WorkflowDto())
    
    val resBoard = new SaveBoardCommand().execute(new SimpleParams(boardDto))
    val board = Board.byId(new ObjectId(resBoard.getPayload().getPayload().getId()), false)
    
    val classOfServiceDto = new ClassOfServiceDto(
    		null,
    		"name 1",
    		"description 1",
    		"color 1",
    		1
    )
    
    val resClassOfService = new SaveClassOfServiceCommand().execute(new SimpleParams(classOfServiceDto))
    assert(ClassOfService.all.size === 1)
    assert(ClassOfService.all.head.name === "name 1")
    
    resClassOfService.getPayload().getPayload().setName("name 2")
    val renamedClassOfService = new SaveClassOfServiceCommand().execute(new SimpleParams(resClassOfService.getPayload().getPayload()))
    assert(ClassOfService.all.head.name === "name 2")
    
    new DeleteClassOfServiceCommand().execute(new SimpleParams(renamedClassOfService.getPayload().getPayload()))
    assert(ClassOfService.all.size === 0)
  }
  
   after {
    // cleanup database
    DbCleaner.clearDb
  }
}