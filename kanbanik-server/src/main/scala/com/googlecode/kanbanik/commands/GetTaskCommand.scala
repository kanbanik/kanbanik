package com.googlecode.kanbanik.commands

import org.bson.types.ObjectId

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.dtos.{ErrorDto, TaskDto}

class GetTaskCommand extends Command[TaskDto, TaskDto] with TaskManipulation {

  lazy val taskBuilder = new TaskBuilder

  def execute(taskDto: TaskDto): Either[TaskDto, ErrorDto] = {
    val res = Task.byId(new ObjectId(taskDto.id.get))
    Left(taskBuilder.buildDto2(res))
  }
}