package com.googlecode.kanbanik.api

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import com.googlecode.kanbanik.commands._
import com.googlecode.kanbanik.dto.CommandNames._
import org.apache.shiro.subject.Subject
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.dto.ErrorCodes._
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.dtos.ErrorDto

class KanbanikApi extends HttpServlet {

  implicit val formats = DefaultFormats

  type WithExecute = {def execute(parsedJson: JValue): Either[AnyRef, ErrorDto]}


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

    val json = parse(commandJson)
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

    if (config.onlyLoggedIn) {
      val sessionId: String = extractSessionId(json)

      if (sessionId == null || sessionId == "") {
        respondAppError(ErrorDto("The sessionId has to be set for command: " + commandJson), resp)
        return
      }

      val subject = new Subject.Builder().sessionId(sessionId).buildSubject
      if (!subject.isAuthenticated) {
        respond(ErrorDto("The user is not logged in!"), resp, USER_NOT_LOGGED_IN_STATUS)
        return
      }
    }

    try {
      command.execute(json)
      match {
        case Left(x) => resp.getWriter().print(write(x))
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
        // todo log
      }
    }
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

  val commands = Map[String, (WithExecute, CommandConfiguration)](
    LOGIN.name ->(new LoginCommand(), CommandConfiguration(false)),
    LOGOUT.name ->(new LogoutCommand(), CommandConfiguration(true)),

    // user
    GET_CURRENT_USER.name ->(new GetCurrentUserCommand(), CommandConfiguration(true)),
    CREATE_USER.name ->(new CreateUserCommand(), CommandConfiguration(true)),
    EDIT_USER.name ->(new EditUserCommand(), CommandConfiguration(true)),
    DELETE_USER.name ->(new DeleteUserCommand(), CommandConfiguration(true)),
    GET_ALL_USERS_COMMAND.name ->(new GetAllUsersCommand(), CommandConfiguration(true)),

    // class of service
    GET_ALL_CLASS_OF_SERVICE.name ->(new GetAllClassOfServices(), CommandConfiguration(true)),
    EDIT_CLASS_OF_SERVICE.name ->(new SaveClassOfServiceCommand(), CommandConfiguration(true)),
    CREATE_CLASS_OF_SERVICE.name ->(new SaveClassOfServiceCommand(), CommandConfiguration(true)),
    DELETE_CLASS_OF_SERVICE.name ->(new DeleteClassOfServiceCommand(), CommandConfiguration(true)),

    // project
    GET_ALL_PROJECTS.name ->(new GetAllProjectsCommand(), CommandConfiguration(true)),
    EDIT_PROJECT.name ->(new SaveProjectCommand(), CommandConfiguration(true)),
    CREATE_PROJECT.name ->(new SaveProjectCommand(), CommandConfiguration(true)),
    DELETE_PROJECT.name ->(new DeleteProjectCommand(), CommandConfiguration(true)),
    ADD_PROJECT_TO_BOARD.name ->(new AddProjectsToBoardCommand(), CommandConfiguration(true)),
    REMOVE_PROJECT_FROM_BOARD.name ->(new RemoveProjectFromBoardCommand(), CommandConfiguration(true))

  )

  case class CommandConfiguration(onlyLoggedIn: Boolean)

}
