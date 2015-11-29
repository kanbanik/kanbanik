package com.googlecode.kanbanik.api


import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.security.KanbanikRealm
import net.liftweb.json._
import net.liftweb.json.Serialization.write
import com.googlecode.kanbanik.commands._
import com.googlecode.kanbanik.dto.CommandNames._
import org.apache.shiro.SecurityUtils
import org.apache.shiro.mgt.DefaultSecurityManager
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext;
import com.googlecode.kanbanik.dto.ErrorCodes._
import com.googlecode.kanbanik.exceptions.MidAirCollisionException
import com.googlecode.kanbanik.dtos.{ErrorDto, EventDto}
import org.atmosphere.cpr.{AtmosphereResource, BroadcasterFactory, Broadcaster, PerRequestBroadcastFilter}
import org.atmosphere.cpr.BroadcastFilter.BroadcastAction
import java.util.zip.GZIPOutputStream
import java.nio.charset.Charset
import scala.collection.JavaConversions._

class KanbanikApi extends HttpServlet {

  implicit val formats = DefaultFormats

  type AnyCommand = {
    def execute(parsedJson: JValue, user: User): Either[AnyRef, ErrorDto]
    def filterResult(res: Any, user: User): Boolean
  }

  val factory = BroadcasterFactory.getDefault
  val broadcaster: Broadcaster = factory.lookup("/events")

  broadcaster.getBroadcasterConfig.addFilter(AuthorizationFilter)
  broadcaster.getBroadcasterConfig.addFilter(DontNotifyYourselfFilter)
  broadcaster.getBroadcasterConfig.addFilter(ToJsonFilter)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    process(req, resp)
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse) = {
    process(req, resp)
  }

  def respondAppError(data: ErrorDto, resp: HttpServletResponse) {
    resp.setStatus(APP_ERROR_STATUS)
    resp.getWriter.print(write(data))
  }

  def respond(data: AnyRef, resp: HttpServletResponse, status: Int) {
    resp.setStatus(status)
    resp.getWriter.print(write(data))
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
      case _ : Throwable =>
        respondAppError(ErrorDto("Error parsing input data: " + commandJson), resp)
        return
    }

    val commandName = try {
      (json \ "commandName").extract[String]
    } catch {
      case _: Throwable =>
        respondAppError(ErrorDto("The command name has to be defined! Received data: " + commandJson), resp)
        return
    }

    val commandWithConfig = commands.get(commandName)
    if (!commandWithConfig.isDefined) {
      respondAppError(ErrorDto("Incorrect command name: " + commandName), resp)
      return
    }

    val (command, config) = commandWithConfig.get

    val sessionId: String = extractSessionId(json)

    val user: User = if (sessionId == null || sessionId == "") {
      User.unlogged
    } else {
      val subject = new Subject.Builder().sessionId(sessionId).buildSubject
      if (!subject.isAuthenticated) {
        respond(ErrorDto("The user is not logged in!"), resp, USER_NOT_LOGGED_IN_STATUS)
        return
      }
      ThreadContext.bind(subject)
      // cleares the cached permissions of the user before every command call since the permission might be changed
      // not efficient but very simple - when starts to be a bottleneck need a better solution
      val cached = subject.getPrincipal.asInstanceOf[User]
      User.byId(cached.name)
    }

    try {
      command.execute(json, user) match {
        case Left(x) =>
          val response = write(x)
          if (config.notifyByEvent) {
            notifyClients(commandName, sessionId, x, command.filterResult)
          }

          val encoding = req.getHeader("accept-encoding")
          if (encoding != null && encoding.indexOf("gzip") != -1) {
              resp.setHeader("Content-Encoding", "gzip")
              val gzip = new GZIPOutputStream(resp.getOutputStream())
              gzip.write(response.getBytes(Charset.forName("UTF-8")))
              gzip.close
          } else {
              resp.getWriter.print(response)
          }
        case Right(x) =>
          respondAppError(x, resp)
      }
    } catch {
      case e: MidAirCollisionException =>
        respondAppError(ErrorDto("This item has been modified by a different user. Please refresh your browser to get the current data."), resp)
      case e: Throwable =>
        respondAppError(ErrorDto("Error while executing command: " + commandName + ". Error: " + e.getMessage + ". For details please look at the server logs."), resp)
        e.printStackTrace()
    }
  }

  case class ClientNotificationData(resp: AnyRef, commandName: String, sessionId: String, filter: (Any, User) => Boolean) {
    def asEventDto = write(EventDto(commandName, write(resp)))
  }


  def notifyClients(commandName: String, sessionId: String, resp: AnyRef, filer: (Any, User) => Boolean) {
    val data = ClientNotificationData(resp, commandName, sessionId, filer)
    broadcaster.broadcast(data)
  }

  def extractSessionId(json: JValue): String = {
    try {
      return (json \ "sessionId").extract[String]
    } catch {
      case _: Throwable => return ""
    }

    try {
      return (json \ "sessionId").extract[Option[String]].get
    } catch {
      case _: Throwable => return ""
    }

    ""
  }

  object ToJsonFilter extends PerRequestBroadcastFilter {
    override def filter(broadcasterId: String, r: AtmosphereResource, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {
      filter(broadcasterId, originalMessage, message)
    }

    override def filter(broadcasterId: String, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {
      if (originalMessage == null) {
        new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
      } else {
        originalMessage match {
          case (m: ClientNotificationData) => new BroadcastAction(m.asEventDto)
          case _ => new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
        }
      }

    }
  }

  object DontNotifyYourselfFilter extends PerRequestBroadcastFilter {

    def filter(broadcasterId: String, r: AtmosphereResource, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {

      if (originalMessage == null) {
        new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
      } else {
        originalMessage match {
          case(messageObject : ClientNotificationData) =>
            val originalSessionId = messageObject.sessionId
            val toSendSessionId = extractSessionId(r)

            if (toSendSessionId == null || toSendSessionId == originalSessionId) {
              new BroadcastAction(BroadcastAction.ACTION.ABORT, originalMessage)
            } else {
              new BroadcastAction(message)
            }

          // other than String
          case(_) => new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
        }
      }
    }

    def filter(broadcasterId: String, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {
      new BroadcastAction(message)
    }
  }

  object AuthorizationFilter extends PerRequestBroadcastFilter {

    def filter(broadcasterId: String, r: AtmosphereResource, originalMessage: scala.Any, message: scala.Any): BroadcastAction = {
      val sessionId = extractSessionId(r)
      if (sessionId == null || originalMessage == null) {
        new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
      } else {
        val subject = new Subject.Builder().sessionId(sessionId).buildSubject
        if (subject.isAuthenticated) {
          val cached = subject.getPrincipal.asInstanceOf[User]
          val realUser = User.byId(cached.name)

          val allowed: Boolean = originalMessage match {
            case (m : ClientNotificationData) => m.filter(m.resp, realUser)
            // unconfigured, more safe not to allow at all
            case _  => false
          }

          if (allowed) {
            new BroadcastAction(message)
          } else {
            new BroadcastAction(BroadcastAction.ACTION.ABORT, message)
          }
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

  val commands = Map[String, (AnyCommand, CommandConfiguration)](
    LOGIN.name -> (new LoginCommand(), CommandConfiguration(notifyByEvent = false)),
    LOGOUT.name -> (new LogoutCommand(), CommandConfiguration(notifyByEvent = false)),

    // user
    GET_CURRENT_USER.name -> (new GetCurrentUserCommand(), CommandConfiguration(notifyByEvent = false)),
    CREATE_USER.name -> (new CreateUserCommand(), CommandConfiguration(notifyByEvent = false)),
    EDIT_USER.name -> (new EditUserCommand(), CommandConfiguration(notifyByEvent = false)),
    DELETE_USER.name -> (new DeleteUserCommand(), CommandConfiguration(notifyByEvent = false)),
    GET_ALL_USERS_COMMAND.name -> (new GetAllUsersCommand(), CommandConfiguration(notifyByEvent = false)),

    // class of service
    GET_ALL_CLASS_OF_SERVICE.name -> (new GetAllClassOfServices(), CommandConfiguration(notifyByEvent = false)),
    EDIT_CLASS_OF_SERVICE.name -> (new SaveClassOfServiceCommand(), CommandConfiguration(notifyByEvent = true)),
    CREATE_CLASS_OF_SERVICE.name -> (new SaveClassOfServiceCommand(), CommandConfiguration(notifyByEvent = true)),
    DELETE_CLASS_OF_SERVICE.name -> (new DeleteClassOfServiceCommand(), CommandConfiguration(notifyByEvent = true)),

    // project
    GET_ALL_PROJECTS.name -> (new GetAllProjectsCommand(), CommandConfiguration(notifyByEvent = false)),
    EDIT_PROJECT.name -> (new SaveProjectCommand(), CommandConfiguration(notifyByEvent = true)),
    CREATE_PROJECT.name -> (new SaveProjectCommand(), CommandConfiguration(notifyByEvent = true)),
    DELETE_PROJECT.name -> (new DeleteProjectCommand(), CommandConfiguration(notifyByEvent = true)),
    ADD_PROJECT_TO_BOARD.name -> (new AddProjectsToBoardCommand(), CommandConfiguration(notifyByEvent = true)),
    REMOVE_PROJECT_FROM_BOARD.name -> (new RemoveProjectFromBoardCommand(), CommandConfiguration(notifyByEvent = true)),

    // task
    MOVE_TASK.name -> (new MoveTaskCommand(), CommandConfiguration(notifyByEvent = true)),
    CREATE_TASK.name -> (new SaveTaskCommand(), CommandConfiguration(notifyByEvent = true)),
    EDIT_TASK.name -> (new SaveTaskCommand(), CommandConfiguration(notifyByEvent = true)),
    GET_TASK.name -> (new GetTaskCommand(), CommandConfiguration(notifyByEvent = false)),
    GET_TASKS.name -> (new GetTasksCommand(), CommandConfiguration(notifyByEvent = false)),
    DELETE_TASK.name -> (new DeleteTasksCommand(), CommandConfiguration(notifyByEvent = true)),

    // board / workflowitem
    EDIT_WORKFLOWITEM_DATA.name -> (new EditWorkflowitemDataCommand(), CommandConfiguration(notifyByEvent = true)), // ???
    DELETE_WORKFLOWITEM.name -> (new DeleteWorkflowitemCommand(), CommandConfiguration(notifyByEvent = true)),      // ???
    GET_ALL_BOARDS_WITH_PROJECTS.name -> (new GetAllBoardsCommand(), CommandConfiguration(notifyByEvent = false)),

    CREATE_BOARD.name -> (new SaveBoardCommand(), CommandConfiguration(notifyByEvent = true)),
    EDIT_BOARD.name -> (new SaveBoardCommand(), CommandConfiguration(notifyByEvent = true)),
    DELETE_BOARD.name -> (new DeleteBoardCommand(), CommandConfiguration(notifyByEvent = true)),
    EDIT_WORKFLOW.name -> (new EditWorkflowCommand(), CommandConfiguration(notifyByEvent = true)), // ???
    GET_BOARD.name -> (new GetBoardCommand(), CommandConfiguration(notifyByEvent = false))

  )

  case class CommandConfiguration(notifyByEvent: Boolean)

}
