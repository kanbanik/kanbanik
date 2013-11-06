package com.googlecode.kanbanik.api

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import net.liftweb.json._
import com.googlecode.kanbanik.dtos._
import net.liftweb.json.Serialization.write

class KanbanikApi extends HttpServlet {

  implicit val formats = DefaultFormats

  override def doGet(req : HttpServletRequest, resp : HttpServletResponse) = {
    process(req, resp)
  }

  override def doPost(req : HttpServletRequest, resp : HttpServletResponse) = {
    process(req, resp)
  }

  private def process(req : HttpServletRequest, resp : HttpServletResponse) {
    val command = req.getParameter("command")
    if (command == null) {
      resp.getWriter().print("command has to be set!")
      return
    }

    val json = parse(command)
    val commandName = (json \ "commandName").extract[String]
    val extracted = json.extract[LoginDto]

    val res = SomeResponse("this is a response text")
    write(res)

    resp.getWriter().print(write(res))

  }

}
