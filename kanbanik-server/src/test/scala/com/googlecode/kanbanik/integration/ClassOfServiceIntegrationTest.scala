package com.googlecode.kanbanik.integration

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfter
import com.googlecode.kanbanik.model.DbCleaner
import com.googlecode.kanbanik.commands.SaveClassOfServiceCommand
import com.googlecode.kanbanik.model.ClassOfService
import com.googlecode.kanbanik.commands.DeleteClassOfServiceCommand
import com.googlecode.kanbanik.dtos.ClassOfServiceDto

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
    
    val resClassOfService = new SaveClassOfServiceCommand().execute(classOfServiceDto) match {
      case Left(x) => x
      case Right(x) => fail("SaveClassOfServiceCommand failed")
    }

    assert(ClassOfService.all.size === 1)
    assert(ClassOfService.all.head.name === "name 1")
    
    val renamedClassOfServiceDto = resClassOfService.copy(name = "name 2")

    val renamedClassOfService = new SaveClassOfServiceCommand().execute(renamedClassOfServiceDto) match {
      case Left(x) => x
      case Right(x) => fail("SaveClassOfServiceCommand failed")
    }
    assert(ClassOfService.all.head.name === "name 2")
    
    new DeleteClassOfServiceCommand().execute(renamedClassOfService)
    assert(ClassOfService.all.size === 0)
  }
  
   after {
    // cleanup database
    DbCleaner.clearDb
  }
}