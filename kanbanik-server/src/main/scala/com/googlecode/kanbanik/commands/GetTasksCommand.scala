package com.googlecode.kanbanik.commands

import com.googlecode.kanbanik.builders.TaskBuilder
import com.googlecode.kanbanik.model.Task
import com.googlecode.kanbanik.model.Board
import com.googlecode.kanbanik.dtos.{ErrorDto, GetTasksDto, TasksDto}

class GetTasksCommand extends Command[GetTasksDto, TasksDto] {

  lazy val taskBuilder = new TaskBuilder

  def execute(dto: GetTasksDto): Either[TasksDto, ErrorDto] = {
    val res = for (
      board <- Board.all(includeTasks = true, includeTaskDescription = dto.includeDescription);
      task <- board.tasks) yield taskBuilder.buildDto(task)

    Left(TasksDto(res))
  }
}
