package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.builders.{TaskBuilder, WorkflowitemTestManipulation, ProjectBuilder}
import com.googlecode.kanbanik.commands.AddProjectsToBoardCommand
import com.googlecode.kanbanik.commands.EditWorkflowCommand
import com.googlecode.kanbanik.commands.EditWorkflowitemDataCommand
import com.googlecode.kanbanik.commands.GetAllBoardsCommand
import com.googlecode.kanbanik.commands.MoveTaskCommand
import com.googlecode.kanbanik.commands.SaveBoardCommand
import com.googlecode.kanbanik.commands.SaveProjectCommand
import com.googlecode.kanbanik.commands.DeleteTasksCommand
import com.googlecode.kanbanik.commands.DeleteWorkflowitemCommand
import com.googlecode.kanbanik.commands.DeleteProjectCommand
import com.googlecode.kanbanik.commands.DeleteBoardCommand
import com.googlecode.kanbanik.dto._
import com.googlecode.kanbanik.dto.shell.EditWorkflowParams
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.{ClassOfServiceDto => OldClassOfServiceDto}
import com.googlecode.kanbanik.model.{Task, Board, DbCleaner, Project}
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.commands.SaveClassOfServiceCommand
import com.googlecode.kanbanik.commands.DeleteClassOfServiceCommand
import com.googlecode.kanbanik.commands.SaveTaskCommand
import com.googlecode.kanbanik.commons._
import com.googlecode.kanbanik.commands.GetAllClassOfServices
import com.googlecode.kanbanik.dtos._
import com.googlecode.kanbanik.dto.{ProjectDto => OldProjectDto}
import com.googlecode.kanbanik.dto.{TaskDto => OldTaskDto}
import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.dto.WorkfloVerticalSizing
import com.googlecode.kanbanik.dto.WorkflowitemDto
import scala.Some
import com.googlecode.kanbanik.dto.BoardDto
import com.googlecode.kanbanik.dtos.ProjectDto
import com.googlecode.kanbanik.dtos.ClassOfServiceDto
import com.googlecode.kanbanik.dtos.{UserDto => NewUserDto}
import com.googlecode.kanbanik.dtos.TaskDto
import com.googlecode.kanbanik.dto.ListDto
import com.googlecode.kanbanik.dtos.ManipulateUserDto
import com.googlecode.kanbanik.dto.WorkflowDto
import com.googlecode.kanbanik.dtos.EmptyDto
import org.bson.types.ObjectId

/**
 * This are tests which expects working DB and are trying to simulate some basic use
 * cases of the users. They are calling real commands
 */
@RunWith(classOf[JUnitRunner])
class IntegrationTests extends FlatSpec with BeforeAndAfter with WorkflowitemTestManipulation {
  "Kanbanik" should "be able to create a new setup from scratch use it and delete it" in {
      val start = System.currentTimeMillis()
    // creation phase
    
    // create user
    val storedUser = new CreateUserCommand().execute(ManipulateUserDto("user1", "", null, "", 1, "aaa", "aaa"))

    // create board
    val board = new BoardDto()
    board.setName("board1")
    board.setWorkflow(new WorkflowDto())
    val storedBoard = new SaveBoardCommand().execute(new SimpleParams(board))
    
    // create class of service
    val classOfServiceToCreate = ClassOfServiceDto(null, "eXpedite", "expedite description", "111111", 1, None)

    val storedClassOfService = new SaveClassOfServiceCommand().execute(classOfServiceToCreate) match {
      case Left(x) => x
      case Right(x) => fail()
    }
    
    // create project
    val project = new ProjectDto(None, Some("project1"), None, 1)
    val storedProject = new SaveProjectCommand().execute(project) match {
      case Left(x) => x
      case Right(x) => fail()
    }
    
    // assign project to board
    val projectWithBoard = ProjectWithBoardDto(storedProject, storedBoard.getPayload().getPayload().getId)
    val storedBoardWithProjects = new AddProjectsToBoardCommand().execute(projectWithBoard) match {
      case Left(x) => x
      case Right(x) => fail()
    }

    // create workflow
    val workflow = loadWorkflow()
    val item1 = itemDtoWithName("item1", workflow)
    val item2 = itemDtoWithName("item2", workflow)
    val item3 = itemDtoWithName("item3", workflow)
    
    editWorkflow(itemDtoWithName("item1", loadWorkflow), null, loadWorkflow)
    editWorkflow(itemDtoWithName("item2", loadWorkflow), null, loadWorkflow)
    editWorkflow(itemDtoWithName("item3", loadWorkflow), null, loadWorkflow)
    
    assert(loadAllBoards.map(dto => dto.getBoard.getName()) === List("board1"))
    assert(loadProject().getName() === "project1")

    assert(asWorkflowList(loadWorkflow).map(_.getName()) === List("item1", "item2", "item3"))

    val taskDto = TaskDto(
      None,
      "taskName1",
      "desc",
      None,
      None,
      loadWorkflow.getWorkflowitems().get(0).getId,
      1,
      storedProject.id.get.toString,
      None,
      None,
      None,
      loadWorkflow.getBoard.getId
    )
    
    val storedTask = new SaveTaskCommand().execute(taskDto) match {
      case Left(x) => x
      case Right(x) => fail()
    }

    assert(storedTask.name === "taskName1")
    assert(storedTask.classOfService === null)
    assert(storedTask.assignee === null)
    
    // edit phase

    // edit workflow
    editWorkflow(loadItem(2), loadItem(0), loadWorkflow)
    assert(asWorkflowList(loadWorkflow).map(_.getName()) === List("item3", "item1", "item2"))

    // edit workflowitem
    val itemToEdit = loadItem(1)
    itemToEdit.setName("item1_renamed")
    new EditWorkflowitemDataCommand().execute(new SimpleParams(itemToEdit))
    assert(asWorkflowList(loadWorkflow).map(_.getName()) === List("item3", "item1_renamed", "item2"))
    
    // move task
    val taskToNotMove = toNewTask(loadBoard().getTasks().get(0))
    val taskToMove = taskToNotMove.copy(workflowitemId = loadWorkflow.getWorkflowitems().get(2).getId)
    val moveTaskParams = new MoveTaskDto(taskToMove, null, null)


    val movedTask = new MoveTaskCommand().execute(moveTaskParams) match {
      case Left(x) => x
      case Right(_) => fail()
    }

    val expectedId = loadWorkflow.getWorkflowitems().get(2).getId
    assert(movedTask.workflowitemId === expectedId)
    assert(loadBoard().getTasks().get(0).getWorkflowitem().getName() === "item2")
    
    // edit task
    val taskToEdit = toNewTask(loadBoard().getTasks().get(0))

    val assigneeToTask = storedUser match {
      case Left(storedUserValue) => storedUserValue
      case Right(_) => fail()
    }

    val editedTaskToEdit = taskToEdit.copy(
      assignee = Some(assigneeToTask),
      classOfService = Some(storedClassOfService),
      dueDate = Some("yesterday"))

    val editedTask = new SaveTaskCommand().execute(editedTaskToEdit) match {
      case Left(x) => x
      case Right(_) => fail()
    }

    assert(loadBoard().getTasks().get(0).getDueDate() === "yesterday")
    assert(loadBoard().getTasks().get(0).getClassOfService().getName() === "eXpedite")
    assert(loadBoard().getTasks().get(0).getAssignee().getUserName() === "user1")
    assert(loadBoard().getTasks().get(0).getWorkflowitem().getParentWorkflow().getBoard().isShowUserPictureEnabled() === true)
    
    // edit board
    val boardToEdit = loadBoard()
    boardToEdit.setName("board1_renamed")
    boardToEdit.setWorkfloVerticalSizing(WorkfloVerticalSizing.MIN_POSSIBLE)
    boardToEdit.setShowUserPictureEnabled(false)
    val editedBoard = new SaveBoardCommand().execute(new SimpleParams(boardToEdit))
    assert(editedBoard.getPayload().getPayload().getName() === "board1_renamed")
    assert(editedBoard.getPayload().getPayload().getWorkfloVerticalSizing() === WorkfloVerticalSizing.MIN_POSSIBLE)
    assert(editedBoard.getPayload().getPayload().isShowUserPictureEnabled() === false)
    // verify it did not destroy the workflow
    assert(asWorkflowList(loadWorkflow).map(_.getName()) === List("item3", "item1_renamed", "item2"))
    
    // edit project
    val projectToEdit = loadProject()
    projectToEdit.setName("project1_renamed")
    val editProject = new SaveProjectCommand().execute(toNewProject(projectToEdit)) match {
      case Left(x) => x
      case Right(x) => fail()
    }

    assert(editProject.name === "project1_renamed")
    assert(Project.all.size === 1)
    // delete phase
    
    // delete task
    new DeleteTasksCommand().execute(TasksDto(List(editedTask)))
    assert(loadBoard().getTasks().size() === 0)
    
    // delete workflowitems
    val loadedItem1 = loadItem(0)
    val loadedItem2 = loadItem(1)
    val loadedItem3 = loadItem(2)
    
    new DeleteWorkflowitemCommand().execute(new SimpleParams(loadedItem1))
    new DeleteWorkflowitemCommand().execute(new SimpleParams(loadedItem2))
    new DeleteWorkflowitemCommand().execute(new SimpleParams(loadedItem3))
    
    assert(loadWorkflowitems().size() === 0)
    
    // delete project
    new DeleteProjectCommand().execute(toNewProject(loadProject))
    assert(loadAllBoards.head.getProjectsOnBoard().size() === 0)
    
    // delete class of service
    new DeleteClassOfServiceCommand().execute(storedClassOfService)
    assert(loadAllClassesOfService().size === 0)
    
    // delete board
    new DeleteBoardCommand().execute(new SimpleParams(loadBoard))
    assert(loadAllBoards.size === 0)
  }
  
  def loadProject() = loadAllBoards.head.getProjectsOnBoard().get(0)
  
  def loadBoard() = loadAllBoards().head.getBoard()
  
  def loadWorkflow() = loadAllBoards.head.getBoard().getWorkflow()

  def loadWorkflowitems() = loadBoard().getWorkflow().getWorkflowitems()
  
  def loadItem(item: Int): WorkflowitemDto = {
    loadWorkflowitems().get(item)
  }
  
  def loadAllClassesOfService() = {
    new GetAllClassOfServices().execute(EmptyDto()) match {
      case Left(x) => x.result
      case Right(x) => fail()
    }
  }
  
  def loadAllBoards() = {
	  new GetAllBoardsCommand().execute(new GetAllBoardsWithProjectsParams(true)).getPayload().getList().toScalaList    
  }
  
  def asWorkflowList(workflow: WorkflowDto) = {
    workflow.getWorkflowitems().toScalaList
  }
  
  def editWorkflow(item: WorkflowitemDto, nextItem: WorkflowitemDto, workflow: WorkflowDto) {
    val editWorkflowParams = new EditWorkflowParams(
    		item,
    		nextItem,
    		workflow
    )
    
    new EditWorkflowCommand().execute(editWorkflowParams)
  }

  def toNewTask(oldTask: OldTaskDto): TaskDto = {
    // not a correct way - only until the refactoring is finished
    val taskBuilder = new TaskBuilder
    taskBuilder.buildDto2(Task.byId(new ObjectId(oldTask.getId)))
  }

  def toOldProject(project: ProjectDto): OldProjectDto = {
    val projectBuilder = new ProjectBuilder()
    projectBuilder.buildDto(projectBuilder.buildEntity2(project))
  }

  def toNewProject(project: OldProjectDto): ProjectDto = {
    val projectBuilder = new ProjectBuilder()
    projectBuilder.buildDto2(projectBuilder.buildEntity(project))
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}