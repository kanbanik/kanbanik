package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.dto.shell.Params
import com.googlecode.kanbanik.dto.shell.Result
import com.googlecode.kanbanik.dto.shell.SimpleShell
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.ProjectScala

class NewProjectServerCommand extends ServerCommand[SimpleShell[ProjectDto], SimpleShell[ProjectDto]] {

  def execute(params: SimpleShell[ProjectDto]): SimpleShell[ProjectDto] = {
    val dto = params.getPayload;
    val stored = new ProjectScala(
        None,
        dto.getName,
        None,
        None).store;
    
    new SimpleShell(new ProjectDto(stored.name))
  }
}