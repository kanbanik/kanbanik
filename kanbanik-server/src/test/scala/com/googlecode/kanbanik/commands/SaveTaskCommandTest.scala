package com.googlecode.kanbanik.commands

import org.scalatest.FlatSpec
import com.googlecode.kanbanik.dtos.MoveTaskDto

class SaveTaskCommandTest extends FlatSpec {
  private val command = new MoveTaskCommand

  "calculateNewOrder" should "use the default order if the only in the worklfowitem" in {
    val res = command.calculateNewOrder(new MoveTaskDto(null, None, None))
    assert(res === "0")
  }

  it should "use next - 100 if it is the first task of couple of tasks" in {
    assert(command.calculateNewOrder(new MoveTaskDto(null, None, Some("350"))) === "250")
    assert(command.calculateNewOrder(new MoveTaskDto(null, None, Some("-150.3"))) === "-250.3")
  }
  
  it should "use prev + 100 if it is the last task of couple of tasks" in {
    assert(command.calculateNewOrder(new MoveTaskDto(null, Some("350"), None)) === "450")
    assert(command.calculateNewOrder(new MoveTaskDto(null, Some("-150.3"), None)) === "-50.3")
  }
  
  it should "put it between the two tasks if both are defined" in {
    assert(command.calculateNewOrder(new MoveTaskDto(null, Some("50"), Some("100"))) === "75")
    assert(command.calculateNewOrder(new MoveTaskDto(null, Some("-50"), Some("-100"))) === "-75")
    assert(command.calculateNewOrder(new MoveTaskDto(null, Some("-200"), Some("300"))) === "50")
  }
}
