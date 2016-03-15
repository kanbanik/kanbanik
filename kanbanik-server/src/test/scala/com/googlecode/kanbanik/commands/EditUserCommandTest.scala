package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.model.Permission
import com.googlecode.kanbanik.dtos.PermissionType
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner

// intentionally commented - contains tests which are failing because the logic is not yet implemented
//@RunWith(classOf[JUnitRunner])
class EditUserCommandTest extends FlatSpec {

  val tested = new EditUserCommand()

  "merge" should "return the same if the edited has no permissions" in {
    val res = tested.merge(
      List(Permission(PermissionType.ReadUser, List())),
      List()
    )

    assert(res == List(Permission(PermissionType.ReadUser, List())))
  }

  "merge" should "return empty list if the edited has all permissions already" in {
    val res = tested.merge(
      List(Permission(PermissionType.ReadUser, List())),
      List(Permission(PermissionType.ReadUser, List()))
    )

    assert(res == List())
  }

  "merge" should "return proper subset" in {
    val res = tested.merge(
      List(
        Permission(PermissionType.ReadUser, List()),
        Permission(PermissionType.CreateBoard, List()),
        Permission(PermissionType.CreateTask_p, List())
      ),

      List(Permission(PermissionType.ReadUser, List()))
    )

    assert(res == List(
      Permission(PermissionType.CreateBoard, List()),
      Permission(PermissionType.CreateTask_p, List())))
  }

  "merge" should "handle also the args" in {
    val res = tested.merge(
      List(
        Permission(PermissionType.ReadUser, List("a", "b"))
      ),

      List(Permission(PermissionType.ReadUser, List("b")))
    )

    assert(res == List(
      Permission(PermissionType.ReadUser, List("a"))))
  }

  "merge" should "add to check permissions also the removed ones" in {
    val res = tested.merge(
      List(
        Permission(PermissionType.ReadUser, List())
      ),

      List(Permission(PermissionType.CreateBoard, List()))
    )

    assert(res == List(
      Permission(PermissionType.ReadUser, List()),
      Permission(PermissionType.CreateBoard, List())))
  }


  "merge" should "add to check permissions also the removed handling also args" in {
    val res = tested.merge(
      List(
        Permission(PermissionType.ReadUser, List("a"))
      ),

      List(Permission(PermissionType.ReadUser, List("a", "b")))
    )

    assert(res == List(
      Permission(PermissionType.ReadUser, List("b"))
    ))
  }

}
