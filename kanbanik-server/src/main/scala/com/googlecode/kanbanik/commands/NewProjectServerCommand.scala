package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.Project
import com.googlecode.kanbanik.builders.ProjectBuilder
import org.mockito.cglib.beans.BulkBean

class NewProjectServerCommand extends ServerCommand[SimpleParams[ProjectDto], SimpleParams[ProjectDto]] {

  val builder = new ProjectBuilder
  
  def execute(params: SimpleParams[ProjectDto]): SimpleParams[ProjectDto] = {
    val stored = builder.buildEntity(params.getPayload).store
    val storedDto = builder.buildShallowDto(stored)
    new SimpleParams(storedDto)
  }
}