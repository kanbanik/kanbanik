package com.googlecode.kanbanik.security

import org.apache.shiro.realm.AuthenticatingRealm
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import com.googlecode.kanbanik.model.User
import org.apache.shiro.authc.credential.HashedCredentialsMatcher

class KanbanikRealm extends AuthenticatingRealm {

  val credentialsMatcher = new HashedCredentialsMatcher
  credentialsMatcher.setHashAlgorithmName("SHA-512")
  credentialsMatcher.setStoredCredentialsHexEncoded(false)
  credentialsMatcher.setHashIterations(1024)
  setCredentialsMatcher(credentialsMatcher)
  
  override protected def supports(token: AuthenticationToken): Boolean = {
    return token.isInstanceOf[UsernamePasswordToken]
  }
  
  protected def doGetAuthenticationInfo(token: AuthenticationToken): AuthenticationInfo = {
    val usernamePasswordToken = token.asInstanceOf[UsernamePasswordToken]
    val user = User.byId(usernamePasswordToken.getUsername())
    new SimpleAuthenticationInfo(user, user.password, user.salt, getName())
  }

}