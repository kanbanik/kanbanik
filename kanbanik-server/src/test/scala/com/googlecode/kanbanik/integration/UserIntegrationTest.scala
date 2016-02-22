package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.commands.DeleteUserCommand
import com.googlecode.kanbanik.commands.EditUserCommand
import com.googlecode.kanbanik.commands.GetAllUsersCommand
import com.googlecode.kanbanik.model.{Permission, DbCleaner, User}
import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.security.KanbanikRealm
import org.apache.shiro.mgt.DefaultSecurityManager
import com.googlecode.kanbanik.commands.LoginCommand
import com.googlecode.kanbanik.dtos._
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UserIntegrationTest extends FlatSpec with BeforeAndAfter {
  "users" should "should be able to do the whole cycle" in {
    val userDto = ManipulateUserDto(
      "username",
      "real name",
      "some://picture.url",
      Some("session id"),
      1,
      "password",
      "new password",
      Some(List())
    )

    // create the first user
    new CreateUserCommand().execute(userDto, User().withAllPermissions())

    val securityManager = new DefaultSecurityManager(new KanbanikRealm)
    SecurityUtils.setSecurityManager(securityManager)

    val loginRes = new LoginCommand().execute(LoginDto("login", "username", "password"))
    assert(loginRes.isLeft)

    val user = User.byId("username")

    assert(user.realName === "real name")

    // rename it with correct credentials
    new EditUserCommand().execute(userDto.copy(realName = "other name"))

    assert(User.byId("username").realName === "other name")

    // add it's permissions
    val manipulateUserPermissionDto = PermissionDto(PermissionType.EditUserData.id, List())
    val manipulateBoardPermissionDto = PermissionDto(PermissionType.EditBoard.id, List("the board id"))

    val manipulateUserPermission = Permission(PermissionType.EditUserData, List())
    val manipulateBoardPermission = Permission(PermissionType.EditBoard, List("the board id"))

    new EditUserCommand().execute(userDto.copy(
      permissions = Some(List(manipulateUserPermissionDto, manipulateBoardPermissionDto)),
      version = 2,
      password = "new password",
      realName = "other name"
    ))

    assert(User.byId("username").permissions === List(manipulateUserPermission, manipulateBoardPermission))

    // try to create existing user
    val createExistingUserRes = new CreateUserCommand().execute(userDto, User().withAllPermissions())
    assert(createExistingUserRes.isRight)
    assertNumOfUsersIs(1)

    // delete this only user should fail
    val deleteLastUserResult = new DeleteUserCommand().execute(UserDto(
      "username",
      "real name",
      "some://picture.url",
      "session id",
      3,
      None,
      Some(false)
    ), User().withAllPermissions())

    assert(deleteLastUserResult.isLeft === false)

    new CreateUserCommand().execute(userDto.copy(userName = "otherUser"), User().withAllPermissions())
    assertNumOfUsersIs(2)

    new DeleteUserCommand().execute(UserDto(
      "username",
      "real name",
      "some://picture.url",
      "session id",
      3,
      None,
      Some(false)
    ), User().withAllPermissions())
    assertNumOfUsersIs(1)

  }

  def assertNumOfUsersIs(expected: Int) {
    new GetAllUsersCommand().execute(SessionDto(None), User().withAllPermissions()) match {
      case Left(allUsers) => assert(allUsers.values.size === expected)
      case Right(_) => ???
    }
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}