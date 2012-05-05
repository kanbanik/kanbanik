package com.googlecode.kanbanik.commands
import com.googlecode.kanbanik.dto.shell.FailableResult
import com.googlecode.kanbanik.dto.shell.SimpleParams
import com.googlecode.kanbanik.dto.shell.VoidParams
import com.googlecode.kanbanik.dto.ProjectDto
import com.googlecode.kanbanik.model.ProjectScala
import org.bson.types.ObjectId

class DeleteProjectCommand extends ServerCommand[SimpleParams[ProjectDto], FailableResult[VoidParams]] {
  def execute(params: SimpleParams[ProjectDto]): FailableResult[VoidParams] = {
    val project = ProjectScala.byId(new ObjectId(params.getPayload().getId()))
    val tasks = project.tasks
    if (!tasks.isDefined) {
      project.delete
      return new FailableResult(new VoidParams, true, "")
    }

    var msg = "There are some tasks associated with this project. Please delete them first and than try to delete the project again. The tasks: [";
    for (task <- tasks.get) {
      msg += task.ticketId + ", ";
    }
    msg = msg.substring(0, msg.length() - 2);
    msg += "]";

    return new FailableResult(new VoidParams, false, msg)
  }
}