package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
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
import com.googlecode.kanbanik.model._
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.commands.SaveClassOfServiceCommand
import com.googlecode.kanbanik.commands.DeleteClassOfServiceCommand
import com.googlecode.kanbanik.commands.SaveTaskCommand
import com.googlecode.kanbanik.commands.GetAllClassOfServices
import com.googlecode.kanbanik.dtos._
import org.scalatest.junit.JUnitRunner

/**
 * This are tests which expects working DB and are trying to simulate some basic use
 * cases of the users. They are calling real commands
 */
@RunWith(classOf[JUnitRunner])
class IntegrationTest extends FlatSpec with BeforeAndAfter with WorkflowitemTestManipulation {
  "Kanbanik" should "be able to create a new setup from scratch use it and delete it" in {
      val start = System.currentTimeMillis()
    // creation phase
    
    // create user
    val storedUser = new CreateUserCommand().execute(ManipulateUserDto("user1", "", null, Some(""), 1, "aaa", "aaa", None),
      User().withAllPermissions())

    // create board
    val board = BoardDto(
      None,
      1,
      "board1",
      1,
      None,
      Some(true),
      Some(false),
      None
    )

    val storedBoard = new SaveBoardCommand().execute(board, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(_) => fail()
    }
    
    // create class of service
    val classOfServiceToCreate = ClassOfServiceDto(null, "eXpedite", "expedite description", "111111", 1, None)

    val storedClassOfService = new SaveClassOfServiceCommand().execute(classOfServiceToCreate, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(x) => fail()
    }
    
    // create project
    val project = new ProjectDto(None, Some("project1"), None, 1)
    val storedProject = new SaveProjectCommand().execute(project, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(x) => fail()
    }
    
    // assign project to board
    val projectWithBoard = ProjectWithBoardDto(storedProject, storedBoard.id.get.toString)
    val storedBoardWithProjects = new AddProjectsToBoardCommand().execute(projectWithBoard, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(x) => fail()
    }

    // create workflow

    editWorkflow(itemDtoWithName("item1", Some(loadWorkflow)), None, loadWorkflow)
    editWorkflow(itemDtoWithName("item2", Some(loadWorkflow)), None, loadWorkflow)
    editWorkflow(itemDtoWithName("item3", Some(loadWorkflow)), None, loadWorkflow)
    
    assert(loadAllBoards.map(dto => dto.board.name) === List("board1"))
    assert(loadProject().name.get === "project1")

    assert(asWorkflowList(loadWorkflow).map(_.name) === List("item1", "item2", "item3"))

    val taskTag1 = TaskTag(None, "t1", Some("d1"), Some("p1"), Some("ou1"), Some(1), Some("red"))

    val taskTag2 = TaskTag(None, "t2", Some("d2"), Some("p2"), Some("ou2"), Some(2), None)

    val taskDto = TaskDto(
      None,
      "taskName1",
      Some("desc"),
      None,
      None,
      loadWorkflow.workflowitems.get.head.id.get,
      1,
      storedProject.id.get.toString,
      None,
      None,
      None,
      loadWorkflow.board.id.get,
      Some(List(taskTag1, taskTag2))
    )

    val storedTask = new SaveTaskCommand().execute(taskDto) match {
      case Left(x) => x
      case Right(x) => fail()
    }

    assert(storedTask.name === "taskName1")
    assert(!storedTask.classOfService.isDefined)
    assert(!storedTask.assignee.isDefined)


    assert(storedTask.taskTags.get.head.description.get === "d1")
    assert(storedTask.taskTags.get.head.colour.get === "red")

    assert(storedTask.taskTags.get.tail.head.description.get === "d2")
    assert(!storedTask.taskTags.get.tail.head.colour.isDefined)

    // edit phase

    // edit workflow
    editWorkflow(loadItem(2), Some(loadItem(0)), loadWorkflow)
    assert(asWorkflowList(loadWorkflow).map(_.name) === List("item3", "item1", "item2"))

    // edit workflowitem
    val itemToEdit = loadItem(1).copy(name = "item1_renamed")
    new EditWorkflowitemDataCommand().execute(itemToEdit, User().withAllPermissions())
    assert(asWorkflowList(loadWorkflow).map(_.name) === List("item3", "item1_renamed", "item2"))
    
    // move task
    val taskToNotMove = loadBoard().tasks.get.head
    val taskToMove = taskToNotMove.copy(workflowitemId = loadWorkflow.workflowitems.get.tail.head.id.get)
    val moveTaskParams = new MoveTaskDto(taskToMove, None, None)


    val movedTask = new MoveTaskCommand().execute(moveTaskParams, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(_) => fail()
    }

    val expectedId = loadWorkflow.workflowitems.get.tail.head.id.get
    assert(movedTask.workflowitemId === expectedId)
    assert(loadBoard().tasks.get.head.workflowitemId === loadWorkflow.workflowitems.get.tail.head.id.get)
    
    // edit task
    val taskToEdit = loadBoard().tasks.get.head

    val assigneeToTask = storedUser match {
      case Left(storedUserValue) => storedUserValue
      case Right(_) => fail()
    }

    val taskTag2Edited = taskTag2.copy(description = Some("d2_edited"))

    val editedTaskToEdit = taskToEdit.copy(
      assignee = Some(assigneeToTask),
      classOfService = Some(storedClassOfService),
      dueDate = Some("yesterday"),
      taskTags = Some(List(taskTag1, taskTag2Edited)))

    val editedTask = new SaveTaskCommand().execute(editedTaskToEdit) match {
      case Left(x) => x
      case Right(_) => fail()
    }

    val loadedEditedTask = loadBoard().tasks.get.head
    assert(loadedEditedTask.dueDate.get === "yesterday")
    assert(loadedEditedTask.classOfService.get.name === "eXpedite")
    assert(loadedEditedTask.assignee.get.userName === "user1")
    assert(loadedEditedTask.taskTags.get.head.description.get === "d1")
    assert(loadedEditedTask.taskTags.get.tail.head.description.get === "d2_edited")

    // edit board
    val boardToEdit = loadBoard().copy(
      name = "board1_renamed",
      workflowVerticalSizing = WorkfloVerticalSizing.MIN_POSSIBLE.id,
      showUserPictureEnabled = Some(false)
    )

    val editedBoard = new SaveBoardCommand().execute(boardToEdit, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(_) => fail()
    }

    assert(editedBoard.name === "board1_renamed")
    assert(editedBoard.workflowVerticalSizing === WorkfloVerticalSizing.MIN_POSSIBLE.id)
    assert(editedBoard.showUserPictureEnabled.get === false)
    // verify it did not destroy the workflow
    assert(asWorkflowList(loadWorkflow).map(_.name) === List("item3", "item1_renamed", "item2"))
    
    // edit project
    val projectToEdit = loadProject().copy(
      name = Some("project1_renamed")
    )

    val editProject = new SaveProjectCommand().execute(projectToEdit, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(x) => fail()
    }

    assert(editProject.name.get === "project1_renamed")
    assert(Project.all(User().withAllPermissions(), None, None).size === 1)
    // delete phase
    
    // delete task
    new DeleteTasksCommand().execute(TasksDto(List(editedTask)), User().withAllPermissions())
    assert(loadBoard().tasks.get.size === 0)
    
    // delete workflowitems
    val loadedItem1 = loadItem(0)
    val loadedItem2 = loadItem(1)
    val loadedItem3 = loadItem(2)
    
    new DeleteWorkflowitemCommand().execute(loadedItem1, User().withAllPermissions())
    new DeleteWorkflowitemCommand().execute(loadedItem2, User().withAllPermissions())
    new DeleteWorkflowitemCommand().execute(loadedItem3, User().withAllPermissions())
    
    assert(loadWorkflowitems().size === 0)
    
    // delete project
    new DeleteProjectCommand().execute(loadProject, User().withAllPermissions())
    assert(loadAllBoards.head.projectsOnBoard.size === 0)
    
    // delete class of service
    new DeleteClassOfServiceCommand().execute(storedClassOfService, User().withAllPermissions())
    assert(loadAllClassesOfService().size === 0)
    
    // delete board
    new DeleteBoardCommand().execute(loadBoard, User().withAllPermissions())
    assert(loadAllBoards.size === 0)
  }
  
  def loadProject() = loadAllBoards.head.projectsOnBoard.get.values.head
  
  def loadBoard() = loadAllBoards().head.board
  
  def loadWorkflow() = loadAllBoards.head.board.workflow.get

  def loadWorkflowitems() = loadBoard().workflow.get.workflowitems.get
  
  def loadItem(item: Int): WorkflowitemDto = {

    def nth(n: Int, x: List[WorkflowitemDto]): WorkflowitemDto = if (n == 0) x.head else nth(n-1, x.tail)

    nth(item, loadWorkflowitems())
  }
  
  def loadAllClassesOfService() = {
    new GetAllClassOfServices().execute(EmptyDto(), User().withAllPermissions()) match {
      case Left(x) => x.values
      case Right(x) => fail()
    }
  }
  
  def loadAllBoards() = {
	  new GetAllBoardsCommand().execute(new GetAllBoardsWithProjectsDto(Some(true), Some(false), None), User().withAllPermissions()) match {
      case Left(x) => x.values
      case Right(_) => fail()
    }
  }
  
  def asWorkflowList(workflow: WorkflowDto) = {
    workflow.workflowitems.get
  }
  
  def editWorkflow(item: WorkflowitemDto, nextItem: Option[WorkflowitemDto], workflow: WorkflowDto) {
    val editWorkflowParams = new EditWorkflowParams(
    		item,
    		nextItem,
    		workflow,
        loadBoard()
    )
    
    new EditWorkflowCommand().execute(editWorkflowParams, User().withAllPermissions())
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}