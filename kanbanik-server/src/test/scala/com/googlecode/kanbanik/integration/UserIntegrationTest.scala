package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import com.googlecode.kanbanik.dto.ManipulateUserDto
import com.googlecode.kanbanik.commands.CreateUserCommand
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.model.DbCleaner
import com.googlecode.kanbanik.commands.EditUserCommand
import com.googlecode.kanbanik.commands.DeleteUserCommand
import com.googlecode.kanbanik.commands.GetAllUsersCommand
import com.googlecode.kanbanik.dto.shell.VoidParams

@RunWith(classOf[JUnitRunner])
class UserIntegrationTest extends FlatSpec with BeforeAndAfter {
  "users" should "should be able to do the whole cycle" in {

    val userDto = new ManipulateUserDto(
      "username",
      "real name",
      1,
      "password",
      "new password")

    // create the first user
    new CreateUserCommand().execute(new SimpleParams(userDto))

    val user = User.byId("username")

    assert(user.realName === "real name")

    // rename it with correct credentials
    userDto.setRealName("other name")

    new EditUserCommand().execute(new SimpleParams(userDto))

    assert(User.byId("username").realName === "other name")

    // try to rename with incorrect credentials
    userDto.setRealName("other name2")
    userDto.setPassword("incorrect password")
    userDto.setVersion(2)

    new EditUserCommand().execute(new SimpleParams(userDto))

    assert(User.byId("username").realName === "other name")

    // try to create existing user
    val createExistingUserRes = new CreateUserCommand().execute(new SimpleParams(userDto))
    assert(createExistingUserRes.isSucceeded() === false)

    assert(new GetAllUsersCommand().execute(new VoidParams).getPayload().getList().size() === 1)
    
    // delete this user
    new DeleteUserCommand().execute(new SimpleParams(userDto))
    
    assert(new GetAllUsersCommand().execute(new VoidParams).getPayload().getList().size() === 0)
  }

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}