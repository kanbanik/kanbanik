package com.googlecode.kanbanik.commands
import org.bson.types.ObjectId
import com.googlecode.kanbanik.builders.ProjectBuilder
import com.googlecode.kanbanik.messages.ServerMessages
import com.googlecode.kanbanik.model.validation.ProjectValidation
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.dtos.{ErrorDto, EmptyDto, ProjectDto}

class DeleteProjectCommand extends Command[ProjectDto, EmptyDto] with ProjectValidation with HasEntityLoader {

  lazy val projectBuilder = new ProjectBuilder

  def execute(params: ProjectDto): Either[EmptyDto, ErrorDto] = {

    if (!params.id.isDefined) {
      return Right(ErrorDto("The ID of the project has to be set"))
    }

    loadProject(new ObjectId(params.id.get)).getOrElse(return Right(ErrorDto(ServerMessages.entityDeletedMessage("project"))))
    
    val project = projectBuilder.buildEntity(params)
    
    val (deletable, msg) = canBeDeleted(project)
    if (!deletable) {
    	return Right(ErrorDto(msg))
    }

    project.delete()

    Left(EmptyDto())
  }
}