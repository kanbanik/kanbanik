package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.commands.DeleteUserCommand
import com.googlecode.kanbanik.commands.EditUserCommand
import com.googlecode.kanbanik.commands.GetAllUsersCommand
import com.googlecode.kanbanik.model.DbCleaner
import com.googlecode.kanbanik.model.User
import org.apache.shiro.SecurityUtils
import com.googlecode.kanbanik.security.KanbanikRealm
import org.apache.shiro.mgt.DefaultSecurityManager
import com.googlecode.kanbanik.commands.LoginCommand
import com.googlecode.kanbanik.dtos.{UserDto, SessionDto, ManipulateUserDto, LoginDto}

@RunWith(classOf[JUnitRunner])
class UserIntegrationTest extends FlatSpec with BeforeAndAfter {
  "users" should "should be able to do the whole cycle" in {
    val userDto = ManipulateUserDto(
      "username",
      "real name",
      "some://picture.url",
      "session id",
      1,
      "password",
      "new password")

    // create the first user
    new CreateUserCommand().execute(userDto)

    val securityManager = new DefaultSecurityManager(new KanbanikRealm)
    SecurityUtils.setSecurityManager(securityManager)

    val loginRes = new LoginCommand().execute(LoginDto("login", "username", "password"))
    assert(loginRes.isLeft)

    val user = User.byId("username")

    assert(user.realName === "real name")

    // rename it with correct credentials
    new EditUserCommand().execute(userDto.copy(realName = "other name"))

    assert(User.byId("username").realName === "other name")

    // try to rename with incorrect credentials
    new EditUserCommand().execute(userDto.copy(
      realName = "other name2",
      password = "incorrect password",
      version = 2
    ))

    assert(User.byId("username").realName === "other name")

    // try to create existing user
    val createExistingUserRes = new CreateUserCommand().execute(userDto)
    assert(createExistingUserRes.isRight)
    assertNumOfUsersIs(1)

    // delete this only user should fail
    val deleteLastUserResult = new DeleteUserCommand().execute(UserDto(
      "username",
      "real name",
      "some://picture.url",
      "session id",
      1
    ))
    assert(deleteLastUserResult.isLeft === false)

    new CreateUserCommand().execute(userDto.copy(userName = "otherUser"))
    assertNumOfUsersIs(2)

    new DeleteUserCommand().execute(UserDto(
      "username",
      "real name",
      "some://picture.url",
      "session id",
      2
    ))
    assertNumOfUsersIs(1)
  }

  def assertNumOfUsersIs(expected: Int) {
    new GetAllUsersCommand().execute(SessionDto("")) match {
      case Left(allUsers) => assert(allUsers.values.size === expected)
      case Right(_) => ???
    }
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}