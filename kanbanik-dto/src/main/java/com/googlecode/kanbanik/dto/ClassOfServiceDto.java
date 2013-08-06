package com.googlecode.kanbanik.dto;

public class ClassOfServiceDto implements KanbanikDto, IdentifiableDto {
	
	private static final long serialVersionUID = -8018293828285834040L;

	private String id;

	private String name;
	
	private String description;
	
	private String colour;
	
	private int version;
	
	public ClassOfServiceDto() {
		
	}

	public ClassOfServiceDto(String id, String name, String description,
			String colour, int version) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.colour = colour;
		this.version = version;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
}
