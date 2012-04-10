package com.googlecode.kanbanik;

import java.util.Collection;

import javax.persistence.EntityManager;

public class Tasks {

	private Project project;
	
	public Tasks(Project project) {
		this.project = project;
	}
	
	public Collection<Task> all() {
		return project.getTasks();
	}
	
	public Task store(Task newTask, Project project, EntityManager manager) {
		Workflowitem workflowItem = manager.find(Workflowitem.class, newTask.getWorkflowitem().getId());

		if (newTask.getId() == null) {
			return storeNewTask(manager, workflowItem, newTask);
		} else {
			return updateTask(manager, workflowItem, newTask);
		}
	}

	public void delete(Task task, EntityManager manager) {
		Task realTask = manager.find(Task.class, task.getId());
		realTask.getWorkflowitem().removeTask(realTask);
		Project realProject = manager.find(Project.class, project.getId());
		realProject.removeTask(realTask);
		manager.remove(realTask);
	}
	
	private Task updateTask(EntityManager manager, Workflowitem workflowItem, Task newTask) {
		Task task = manager.find(Task.class, newTask.getId());
		task.setWorkflowitem(workflowItem);
		task.setClassOfService(newTask.getClassOfService());
		task.setDescription(newTask.getDescription());
		task.setTicketId(newTask.getTicketId());
		task.setName(newTask.getName());
		return task;
	}

	private Task storeNewTask(EntityManager manager, Workflowitem workflowItem, Task newTask) {
		newTask.setWorkflowitem(workflowItem);
		manager.persist(newTask);
		project.getTasks().add(newTask);
		newTask.setTicketId("#" + project.getName() + "-" + newTask.getId());
		return newTask;
	}

}
