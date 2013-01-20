package com.googlecode.kanbanik.model

import org.apache.shiro.crypto.SecureRandomNumberGenerator
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.util.ByteSource
import org.apache.shiro.crypto.hash.Sha512Hash

class User(
  val name: String,
  val password: String,
  val salt: ByteSource) {

}

object User {

  val rng = new SecureRandomNumberGenerator();

  def byId(name: String) = {

    if (name == "user1") {
      makeUser("user1", "pass1")
    } else if (name == "user2") {
      makeUser("user2", "pass2")
    } else {
      throw new IllegalArgumentException("No such task with name: " + name)
    }

  }

  def makeUser(name: String, pass: String) = {
    val salt = rng.nextBytes()
    val hashedPasswordBase64 = new Sha512Hash(pass, salt, 1024).toBase64()
    new User(name, hashedPasswordBase64, salt)

  }
}