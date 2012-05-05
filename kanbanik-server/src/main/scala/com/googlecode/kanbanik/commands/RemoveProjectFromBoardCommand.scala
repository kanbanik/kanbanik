package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.model.ProjectScala
import com.googlecode.kanbanik.model.BoardScala

class RemoveProjectFromBoardCommand extends BaseProjectsOnBoardCommand {
  
  override def executeSpecific(board: BoardScala, project: ProjectScala) {
    if (project.boards.isDefined) {
      project.boards = Some(project.boards.get.filter(_.id != board.id))
      project.store
    }
  }
}