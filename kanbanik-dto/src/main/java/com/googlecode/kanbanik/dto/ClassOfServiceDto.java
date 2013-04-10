package com.googlecode.kanbanik.dto;

public class ClassOfServiceDto implements KanbanikDto {
	
	private static final long serialVersionUID = -8018293828285834040L;

	private String id;

	private String name;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
