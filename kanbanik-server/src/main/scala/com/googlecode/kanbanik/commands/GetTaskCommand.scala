package com.googlecode.kanbanik.commands

import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.{User, Task}
import com.googlecode.kanbanik.dtos.{ErrorDto, TaskDto}

class GetTaskCommand extends Command[TaskDto, TaskDto] with TaskManipulation {

  lazy val taskBuilder = new TaskBuilder

  override def execute(taskDto: TaskDto, user: User): Either[TaskDto, ErrorDto] = {
    val res = Task.byId(new ObjectId(taskDto.id.get), user)
    Left(taskBuilder.buildDto(res))
  }
}