package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.ProjectScala

class NewProjectServerCommand extends ServerCommand[SimpleParams[ProjectDto], SimpleParams[ProjectDto]] {

  def execute(params: SimpleParams[ProjectDto]): SimpleParams[ProjectDto] = {
    val dto = params.getPayload;
    val stored = new ProjectScala(
        None,
        dto.getName,
        None,
        None).store;
    
    val res = new ProjectDto
    res.setName(stored.name)
    
    new SimpleParams(res)
  }
}