package com.googlecode.kanbanik.learning.shiro

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.mgt.DefaultSecurityManager
import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.apache.shiro.crypto.SecureRandomNumberGenerator
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import com.googlecode.kanbanik.security.KanbanikRealm

@RunWith(classOf[JUnitRunner])
class ShiroLearningTest extends FlatSpec {

  "Shiro" should "bea able to login existing user" in {

//    val mySecurityManager = new DefaultSecurityManager
//    mySecurityManager.setRealm(new KanbanikRealm)
//
//    SecurityUtils.setSecurityManager(mySecurityManager);
//
//    val token = new UsernamePasswordToken("user1", "pass1");
//    val currentUser = SecurityUtils.getSubject();
//    currentUser.login(token);

  }
}

