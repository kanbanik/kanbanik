package com.googlecode.kanbanik;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;

public class Projects {
	
	private Collection<Project> projects;

	public Projects(Collection<Project> projects) {
		super();
		this.projects = projects;
	}
	
	public Project byId(int id) {
		for (Project project : projects) {
			if (project.getId() == id) {
				return project;
			}
		}
		
		throw new IllegalArgumentException("There is no such project with ID: '" + id + "'");
	}

	public Collection<Project> all() {
		if (projects == null) {
			return new ArrayList<Project>();
		}
		return projects;
	}
	
	public Project store(Project project, EntityManager manager) {
		if (manager.find(Project.class, project.getId()) != null) {
			return edit(project, manager);
		} else {
			manager.persist(project);
			return project;
		}

	}

	private Project edit(Project project, EntityManager manager) {
		Project realProject = manager.find(Project.class, project.getId());
		realProject.setName(project.getName());
		return realProject;
	}

	public ReturnObject delete(Project project, EntityManager manager) {
		Project realProject = manager.find(Project.class, project.getId());
		if (realProject == null) {
			throw new IllegalArgumentException("The project you try to delete does not exist! Project Id: " + project.getId());
		}
		
		if (realProject.getTasks() != null && realProject.getTasks().size() != 0) {
			String msg = "There are some tasks associated with this project. Please delete them first and than try to delete the project again. The tasks: [";
			for (Task task : realProject.getTasks()) {
				msg += task.getTicketId() + ", ";
			}
			msg = msg.substring(0, msg.length() - 2);
			msg += "]";
			return new ReturnObject(false, msg);
		}
		
		manager.remove(realProject);
		return new ReturnObject(true, "");
	}
}
