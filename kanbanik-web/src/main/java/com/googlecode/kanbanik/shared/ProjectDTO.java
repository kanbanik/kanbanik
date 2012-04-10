package com.googlecode.kanbanik.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectDTO implements Serializable {

	private static final long serialVersionUID = 1776310079012968706L;

	private String name;
	
	private int id;
	
	private List<TaskDTO> tasks = new ArrayList<TaskDTO>();;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TaskDTO> getTasks() {
		return tasks;
	}

	public void addTask(TaskDTO task) {
		if (tasks == null) {
			tasks = new ArrayList<TaskDTO>();
		}
		
		tasks.add(task);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectDTO other = (ProjectDTO) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
