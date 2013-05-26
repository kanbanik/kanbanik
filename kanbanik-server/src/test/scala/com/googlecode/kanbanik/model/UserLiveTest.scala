package com.googlecode.kanbanik.model

import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter
import com.googlecode.kanbanik.exceptions.MidAirCollisionException

@RunWith(classOf[JUnitRunner])
class UserLiveTest extends FlatSpec with BeforeAndAfter {
  "store()" should "create a new user if does not exist" in {
    mkDefaultUser.store

    assert(User.byId("name").name === "name")
    assert(User.byId("name").password === "password")
    assert(User.byId("name").realName === "real name")
    assert(User.byId("name").pictureUrl === "url://some.png")
    assert(User.byId("name").salt === "salt")
    assert(User.byId("name").version === 1)
  }

  it should "edit a new user if exists" in {
    val user = mkDefaultUser.store

    user.withPassword("newPass", "newSalt").withRealName("new real name").withPictureUrl("other").store

    assert(User.byId("name").name === "name")
    assert(User.byId("name").password === "newPass")
    assert(User.byId("name").realName === "new real name")
    assert(User.byId("name").pictureUrl === "other")
    assert(User.byId("name").salt === "newSalt")
    assert(User.byId("name").version === 2)
  }

  it should "guard mid air collision" in {
    val user = mkDefaultUser.store

    val user1 = User.byId("name")
    val user2 = User.byId("name")

    user1.store
    intercept[MidAirCollisionException] {
      user2.store
    }

  }

  "delete() " should "delete the user if exists" in {
    mkDefaultUser.store

    User.byId("name").delete

    intercept[IllegalArgumentException] {
      // does not exist anymore
      User.byId("name")
    }
  }

  it should "fail if does not exist" in {
    intercept[IllegalArgumentException] {
      User.byId("name").delete
    }
  }
  
  it should "guard mid air collisions" in {
    mkDefaultUser.store
    
    val user1 = User.byId("name")
    val user2 = User.byId("name")
    
    user1.withRealName("other").store
    
    intercept[MidAirCollisionException] {
      user2.delete
    }
  }

  def mkDefaultUser = new User(
    "name",
    "password",
    "real name",
    "url://some.png",
    "salt",
    1)

  after {
    // cleanup database
    DbCleaner.clearDb
  }
}