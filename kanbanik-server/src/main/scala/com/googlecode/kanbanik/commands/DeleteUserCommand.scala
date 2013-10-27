package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.UserBuilder
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.ManipulateUserDto
import com.googlecode.kanbanik.dto.UserDto
import com.googlecode.kanbanik.model.User
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.model.Board

class DeleteUserCommand extends ServerCommand[SimpleParams[UserDto], FailableResult[VoidParams]] with CredentialsUtils {
  
  lazy val userBuilder = new UserBuilder
  
  def execute(params: SimpleParams[UserDto]): FailableResult[VoidParams] = {
    if(User.all.size > 1) {
    	val user = User.byId(params.getPayload().getUserName())
    	
    	// this is an expensive operation - consider to have this info on the user directly to speed things up
    	val tasksOfUser = for (board <- Board.all(true); task <- board.tasks if (task.assignee.getOrElse(User()).name == user.name)) yield task
    	if (tasksOfUser.size == 0) {
    		user.copy(version = params.getPayload().getVersion()).delete
    		new FailableResult(new VoidParams)  
    	} else {
    	  new FailableResult(new VoidParams, false, "This user has tasks assigned - please delete them firs. Tasks: " + tasksOfUser.map(_.ticketId).mkString(", "))
    	}
    } else {
      new FailableResult(new VoidParams, false, "You can not delete the last user from the system - it would not be possible to log in again!")
    } 
  }

}