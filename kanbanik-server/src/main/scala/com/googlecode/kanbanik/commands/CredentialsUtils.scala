package com.googlecode.kanbanik.commands

import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.crypto.SecureRandomNumberGenerator
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.util.ByteSource
import com.googlecode.kanbanik.security.KanbanikRealm
import com.googlecode.kanbanik.model.User

trait CredentialsUtils {

  val rng = new SecureRandomNumberGenerator()

  val realm = new KanbanikRealm()

  def hashPassword(password: String): (String, String) = {
    val salt = rng.nextBytes().toBase64()

    (hash(password, salt), salt)
  }

  def isAuthenticated(name: String, password: String) = {
    val user = User.byId(name)
    hash(password, user.salt) == user.password
  }
  
  def hash(password: String, salt: String) = new Sha512Hash(password, salt, 1024).toBase64()
}