package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.{User, Task, Board}
import com.googlecode.kanbanik.dtos.{ErrorDto, GetTasksDto, TasksDto}

class GetTasksCommand extends Command[GetTasksDto, TasksDto] {

  lazy val taskBuilder = new TaskBuilder

  override def execute(dto: GetTasksDto, user: User): Either[TasksDto, ErrorDto] = {
    val res = for (
      board <- Board.all(includeTasks = true, includeTaskDescription = dto.includeDescription, None, None, user);
      task <- board.tasks) yield taskBuilder.buildDto(task)

    Left(TasksDto(res))
  }
}
