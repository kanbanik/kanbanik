package com.googlecode.kanbanik.integration

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import com.googlecode.kanbanik.model.{User, DbCleaner, ClassOfService}
import com.googlecode.kanbanik.commands.SaveClassOfServiceCommand
import com.googlecode.kanbanik.commands.DeleteClassOfServiceCommand
import com.googlecode.kanbanik.dtos.ClassOfServiceDto
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ClassOfServiceIntegrationTest extends FlatSpec with BeforeAndAfter {

  "class of service" should "should be able to do the whole cycle" in {
    val classOfServiceDto = ClassOfServiceDto(
    		null,
    		"name 1",
    		"description 1",
    		"color 1",
    		1,
        None

    )
    
    val resClassOfService = new SaveClassOfServiceCommand().execute(classOfServiceDto, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(x) => fail("SaveClassOfServiceCommand failed")
    }

    assert(ClassOfService.all(User().withAllPermissions()).size === 1)
    assert(ClassOfService.all(User().withAllPermissions()).head.name === "name 1")
    
    val renamedClassOfServiceDto = resClassOfService.copy(name = "name 2")

    val renamedClassOfService = new SaveClassOfServiceCommand().execute(renamedClassOfServiceDto, User().withAllPermissions()) match {
      case Left(x) => x
      case Right(x) => fail("SaveClassOfServiceCommand failed")
    }
    assert(ClassOfService.all(User().withAllPermissions()).head.name === "name 2")
    
    new DeleteClassOfServiceCommand().execute(renamedClassOfService, User().withAllPermissions())
    assert(ClassOfService.all(User().withAllPermissions()).size === 0)
  }
  
   after {
    // cleanup database
    DbCleaner.clearDb
  }
}