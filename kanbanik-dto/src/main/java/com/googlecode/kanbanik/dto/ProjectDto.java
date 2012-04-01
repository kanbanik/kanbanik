package com.googlecode.kanbanik.dto;

import java.io.Serializable;

public class ProjectDto implements Serializable {
	
	private static final long serialVersionUID = -313404181329311299L;
	
	private String name;
	
	public ProjectDto(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
