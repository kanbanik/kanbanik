package com.googlecode.kanbanik.commands

import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.dto.TaskDto
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.model.Task

class GetTaskCommand extends ServerCommand[SimpleParams[TaskDto], FailableResult[SimpleParams[TaskDto]]] {
  lazy val taskBuilder = new TaskBuilder

  def execute(params: SimpleParams[TaskDto]): FailableResult[SimpleParams[TaskDto]] = {
    val res = Task.byId(new ObjectId(params.getPayload().getId()))
    new FailableResult(new SimpleParams(taskBuilder.buildDto(res)))
  }
}