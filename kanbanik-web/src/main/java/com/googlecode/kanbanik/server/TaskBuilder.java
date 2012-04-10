package com.googlecode.kanbanik.server;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.kanbanik.ClassOfService;
import com.googlecode.kanbanik.Itemleaf;
import com.googlecode.kanbanik.Kanbanik;
import com.googlecode.kanbanik.Project;
import com.googlecode.kanbanik.Task;
import com.googlecode.kanbanik.Workflowitem;
import com.googlecode.kanbanik.shared.ClassOfServiceDTO;
import com.googlecode.kanbanik.shared.ProjectDTO;
import com.googlecode.kanbanik.shared.TaskDTO;
import com.googlecode.kanbanik.shared.WorkflowItemPlaceDTO;


public class TaskBuilder {
	
	private static Map<ClassOfService, ClassOfServiceDTO> classOfServiceMap;

	private Kanbanik kanbanik;
	
	static {
		classOfServiceMap = new HashMap<ClassOfService, ClassOfServiceDTO>();
		classOfServiceMap.put(ClassOfService.EXPEDITE, ClassOfServiceDTO.EXPEDITE);
		classOfServiceMap.put(ClassOfService.FIXED_DELIVERY_DATE, ClassOfServiceDTO.FIXED_DELIVERY_DATE);
		classOfServiceMap.put(ClassOfService.STANDARD, ClassOfServiceDTO.STANDARD);
		classOfServiceMap.put(ClassOfService.INTANGIBLE, ClassOfServiceDTO.INTANGIBLE);
	}
	
	public TaskDTO build(Task task, Project project) {
		WorkflowItemPlaceDTO place = new WorkflowItemPlaceDTO();
		place.setId(task.getWorkflowitem().getId());
		ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setId(project.getId());
		projectDTO.setName(project.getName());
		TaskDTO taskDTO = build(task, place);
		taskDTO.setProject(projectDTO);
		return taskDTO;
	}
	
	public TaskDTO build(Task task, WorkflowItemPlaceDTO place) {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setName(task.getName());
		taskDTO.setPlace(place);
		taskDTO.setId(task.getId());
		taskDTO.setTicketId(task.getTicketId());
		taskDTO.setDescription(task.getDescription());
		taskDTO.setClassOfService(classOfServiceMap.get(task.getClassOfService()));
		return taskDTO;
	}
	
	public Task build(TaskDTO taskDTO) {
		Project project = kanbanik.getProjects().byId(taskDTO.getProject().getId());
		if (project == null) {
			throw new IllegalArgumentException("The project " + taskDTO.getProject().getId() + " does not exist so the task can not be stored");
		}

		Task task = new Task();
		if (taskDTO.getId() == -1) {
			task.setId(null);	
		} else {
			task.setId(taskDTO.getId());
		}
		
		task.setName(taskDTO.getName());
		
		Workflowitem item = new Itemleaf();
		item.setId(taskDTO.getPlace().getId());
		task.setWorkflowitem(item);
		
		task.setDescription(taskDTO.getDescription());
		task.setTicketId(taskDTO.getTicketId());
		task.setClassOfService(toClassOfServiceDTO(taskDTO.getClassOfService()));
		
		return task;
	}

	private ClassOfService toClassOfServiceDTO(ClassOfServiceDTO classOfService) {
		for (Map.Entry<ClassOfService, ClassOfServiceDTO> entry : classOfServiceMap.entrySet()) {
			if (entry.getValue() == classOfService) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void setKanbanik(Kanbanik kanbanik) {
		this.kanbanik = kanbanik;
	}
}
