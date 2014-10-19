package com.googlecode.kanbanik.api


import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import com.googlecode.kanbanik.commands._
import com.googlecode.kanbanik.dto.CommandNames._
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext;
import com.googlecode.kanbanik.dto.ErrorCodes._
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.dtos.{ErrorDto, EventDto}
import org.atmosphere.cpr.{AtmosphereResource, BroadcasterFactory, Broadcaster, PerRequestBroadcastFilter}
import org.atmosphere.cpr.BroadcastFilter.BroadcastAction

class KanbanikApi extends HttpServlet {

  implicit val formats = DefaultFormats

  type WithExecute = {def execute(parsedJson: JValue): Either[AnyRef, ErrorDto]}

  broadcaster.getBroadcasterConfig.addFilter(AuthorizationFilter)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    process(req, resp)
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    process(req, resp)
  }

  def respondAppError(data: ErrorDto, resp: HttpServletResponse) {
    resp.setStatus(APP_ERROR_STATUS)
    resp.getWriter().print(write(data))
  }

  def respond(data: AnyRef, resp: HttpServletResponse, status: Int) {
    resp.setStatus(status)
    resp.getWriter().print(write(data))
  }

  private def process(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.setCharacterEncoding("UTF-8")
    val commandJson = req.getParameter("command")
    if (commandJson == null) {
      respondAppError(ErrorDto("command has to be set!"), resp)
      return
    }

    val json = try {
      parse(commandJson)
    }  catch {
      case _ : Throwable => {
        respondAppError(ErrorDto("Error parsing input data: " + commandJson), resp)
        return
      }
    }

    val commandName = try {
      (json \ "commandName").extract[String]
    } catch {
      case _: Throwable => {
        respondAppError(ErrorDto("The command name has to be defined! Received data: " + commandJson), resp)
        return
      }
    }

    val commandWithConfig = commands.get(commandName)
    if (!commandWithConfig.isDefined) {
      respondAppError(ErrorDto("Incorrect command name: " + commandName), resp)
      return
    }

    val (command, config) = commandWithConfig.get

    val sessionId: String = extractSessionId(json)

    if (config.onlyLoggedIn) {
      if (sessionId == null || sessionId == "") {
        respondAppError(ErrorDto("The sessionId has to be set for command: " + commandJson), resp)
        return
      }

      val subject = new Subject.Builder().sessionId(sessionId).buildSubject
      if (!subject.isAuthenticated) {
        respond(ErrorDto("The user is not logged in!"), resp, USER_NOT_LOGGED_IN_STATUS)
        return
      }

      ThreadContext.bind(subject)
    }

    try {
      command.execute(json) match {
        case Left(x) => {
          val response = write(x)
          if (config.notifyByEvent) {
            notifyClients(commandName, sessionId, response)
          }

          resp.getWriter().print(response)
        }
        case Right(x) => {
          respondAppError(x, resp)
        }
      }
    } catch {
      case e: MidAirCollisionException => {
        respondAppError(ErrorDto("This item has been modified by a different user. Please refresh your browser to get the current data."), resp)
      }
      case e: Throwable => {
        respondAppError(ErrorDto("Error while executing command: " + commandName + ". Error: " + e.getMessage + ". For details please look at the server logs."), resp)
        e.printStackTrace()
      }
    }
  }

  def notifyClients(commandName: String, sessionId: String, resp: String) {
    val dontNotifyYourself = DontNotifyYourselfFilter(sessionId)
    val b = broadcaster
    b.getBroadcasterConfig.addFilter(dontNotifyYourself)
    b.broadcast(write(EventDto(commandName, resp)))
    b.getBroadcasterConfig.removeFilter(dontNotifyYourself)
  }

  def broadcaster: Broadcaster = {
    val factory = BroadcasterFactory.getDefault
    factory.lookup("/events")
  }

  def extractSessionId(json: JValue): String = {
    val sessionId: String = try {
      return (json \ "sessionId").extract[String]
    } catch {
      case _: Throwable => return ""
    }

    try {
      return (json \ "sessionId").extract[Option[String]].get
    } catch {
      case _: Throwable => return ""
    }

    return ""
  }

  case class DontNotifyYourselfFilter(val currentSessionId: String) extends PerRequestBroadcastFilter {

    def filter(broadcasterId: String, r: AtmosphereResource, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {
      val sessionId = extractSessionId(r)
      if (sessionId == null) {
        new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
      } else {
        if (sessionId.equals(currentSessionId)) {
          new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
        } else {
          new BroadcastAction(message)
        }
      }
    }

    def filter(broadcasterId: String, originalMessage: scala.Any, message: scala.Any): BroadcastAction = new BroadcastAction(message)
  }

  object AuthorizationFilter extends PerRequestBroadcastFilter {

    def filter(broadcasterId: String, r: AtmosphereResource, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {
      val sessionId = extractSessionId(r)
      if (sessionId == null) {
        new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
      } else {
        val subject = new Subject.Builder().sessionId(sessionId).buildSubject
        if (subject.isAuthenticated) {
          new BroadcastAction(message)
        } else {
          new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
        }
      }

    }

    def filter(broadcasterId: String, originalMessage: scala.Any, message: scala.Any): BroadcastAction = new BroadcastAction(message)
  }

  def extractSessionId(r: AtmosphereResource) = {
    val url = r.getRequest.getRequestURL
    if (url == null) {
      null
    } else {
      url.substring(url.lastIndexOf("/") + 1, url.length)
    }
  }

  val commands = Map[String, (WithExecute, CommandConfiguration)](
    LOGIN.name ->(new LoginCommand(), CommandConfiguration(false, false)),
    LOGOUT.name ->(new LogoutCommand(), CommandConfiguration(true, false)),

    // user
    GET_CURRENT_USER.name ->(new GetCurrentUserCommand(), CommandConfiguration(true, false)),
    CREATE_USER.name ->(new CreateUserCommand(), CommandConfiguration(true, false)),
    EDIT_USER.name ->(new EditUserCommand(), CommandConfiguration(true, false)),
    DELETE_USER.name ->(new DeleteUserCommand(), CommandConfiguration(true, false)),
    GET_ALL_USERS_COMMAND.name ->(new GetAllUsersCommand(), CommandConfiguration(true, false)),

    // class of service
    GET_ALL_CLASS_OF_SERVICE.name ->(new GetAllClassOfServices(), CommandConfiguration(true, false)),
    EDIT_CLASS_OF_SERVICE.name ->(new SaveClassOfServiceCommand(), CommandConfiguration(true, true)),
    CREATE_CLASS_OF_SERVICE.name ->(new SaveClassOfServiceCommand(), CommandConfiguration(true, true)),
    DELETE_CLASS_OF_SERVICE.name ->(new DeleteClassOfServiceCommand(), CommandConfiguration(true, true)),

    // project
    GET_ALL_PROJECTS.name ->(new GetAllProjectsCommand(), CommandConfiguration(true, false)),
    EDIT_PROJECT.name ->(new SaveProjectCommand(), CommandConfiguration(true, true)),
    CREATE_PROJECT.name ->(new SaveProjectCommand(), CommandConfiguration(true, true)),
    DELETE_PROJECT.name ->(new DeleteProjectCommand(), CommandConfiguration(true, true)),
    ADD_PROJECT_TO_BOARD.name ->(new AddProjectsToBoardCommand(), CommandConfiguration(true, true)),
    REMOVE_PROJECT_FROM_BOARD.name ->(new RemoveProjectFromBoardCommand(), CommandConfiguration(true, true)),

    // task
    MOVE_TASK.name -> (new MoveTaskCommand(), CommandConfiguration(true, true)),
    CREATE_TASK.name -> (new SaveTaskCommand(), CommandConfiguration(true, true)),
    EDIT_TASK.name -> (new SaveTaskCommand(), CommandConfiguration(true, true)),
    GET_TASK.name -> (new GetTaskCommand(), CommandConfiguration(true, false)),
    GET_TASKS.name -> (new GetTasksCommand(), CommandConfiguration(true, false)),
    DELETE_TASK.name -> (new DeleteTasksCommand(), CommandConfiguration(true, true)),

    // board / workflowitem
    EDIT_WORKFLOWITEM_DATA.name -> (new EditWorkflowitemDataCommand(), CommandConfiguration(true, true)),
    DELETE_WORKFLOWITEM.name -> (new DeleteWorkflowitemCommand(), CommandConfiguration(true, true)),
    GET_ALL_BOARDS_WITH_PROJECTS.name -> (new GetAllBoardsCommand(), CommandConfiguration(true, false)),

    CREATE_BOARD.name -> (new SaveBoardCommand(), CommandConfiguration(true, true)),
    EDIT_BOARD.name -> (new SaveBoardCommand(), CommandConfiguration(true, true)),
    DELETE_BOARD.name -> (new DeleteBoardCommand(), CommandConfiguration(true, true)),
    EDIT_WORKFLOW.name -> (new EditWorkflowCommand(), CommandConfiguration(true, true)),
    GET_BOARD.name -> (new GetBoardCommand(), CommandConfiguration(true, false))

  )

  case class CommandConfiguration(onlyLoggedIn: Boolean, notifyByEvent: Boolean)

}
