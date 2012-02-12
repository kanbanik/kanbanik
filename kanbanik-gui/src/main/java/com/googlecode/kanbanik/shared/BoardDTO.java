package com.googlecode.kanbanik.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BoardDTO implements Serializable {

	private static final long serialVersionUID = 7827525429912859321L;

	private String name;
	
	private int id;

	private List<ProjectDTO> projects = new ArrayList<ProjectDTO>();

	private WorkflowDTO workflow;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProjectDTO> getProjects() {
		return projects;
	}

	public void addProject(ProjectDTO project) {
		projects.add(project);
	}

	public WorkflowDTO getWorkflow() {
		return workflow;
	}

	public void setWorkflow(WorkflowDTO workflow) {
		this.workflow = workflow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
