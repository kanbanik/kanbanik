package com.googlecode.kanbanik.commands

import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.dto.shell.MoveTaskParams

@RunWith(classOf[JUnitRunner])
class SaveTaskCommandTest extends FlatSpec {
  private val command = new MoveTaskCommand

  "calculateNewOrder" should "use the default order if the only in the worklfowitem" in {
    val res = command.calculateNewOrder(new MoveTaskParams(null, null, null, null))
    assert(res === "0")
  }

  it should "use next - 100 if it is the first task of couple of tasks" in {
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, null, "350")) === "250")
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, null, "-150.3")) === "-250.3")
  }
  
  it should "use prev + 100 if it is the last task of couple of tasks" in {
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, "350", null)) === "450")
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, "-150.3", null)) === "-50.3")
  }
  
  it should "put it between the two tasks if both are defined" in {
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, "50", "100")) === "75")
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, "-50", "-100")) === "-75")
    assert(command.calculateNewOrder(new MoveTaskParams(null, null, "-200", "300")) === "50")
  }
}