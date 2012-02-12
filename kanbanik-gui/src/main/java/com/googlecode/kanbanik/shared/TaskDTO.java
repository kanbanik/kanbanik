package com.googlecode.kanbanik.shared;

import java.io.Serializable;

public class TaskDTO implements Serializable {
	
	private static final long serialVersionUID = 3989983010795463413L;

	private String name;
	
	private String ticketId;
	
	private WorkflowItemPlaceDTO place;
	
	private int id;
	
	private String description;
	
	private ClassOfServiceDTO classOfService;
	
	private ProjectDTO project;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlace(WorkflowItemPlaceDTO place) {
		this.place = place;
	}

	public WorkflowItemPlaceDTO getPlace() {
		return place;
	}

	public ProjectDTO getProject() {
		return project;
	}

	public void setProject(ProjectDTO project) {
		this.project = project;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ClassOfServiceDTO getClassOfService() {
		return classOfService;
	}

	public void setClassOfService(ClassOfServiceDTO classOfService) {
		this.classOfService = classOfService;
	}
}
