package com.googlecode.kanbanik.dto;

public class WorkflowitemDto implements IdentifiableDto {

	private static final long serialVersionUID = 1343045359919670502L;

	private String name;

	private String id;

	private int wipLimit;

	private ItemType itemType;

	private int version;

	private WorkflowDto nestedWorkflow;
	
	private WorkflowDto parentWorkflow;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getWipLimit() {
		return wipLimit;
	}

	public void setWipLimit(int wipLimit) {
		this.wipLimit = wipLimit;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public WorkflowDto getNestedWorkflow() {
		return nestedWorkflow;
	}

	public void setNestedWorkflow(WorkflowDto nestedWorkflow) {
		this.nestedWorkflow = nestedWorkflow;
	}

	public WorkflowDto getParentWorkflow() {
		return parentWorkflow;
	}

	public void setParentWorkflow(WorkflowDto parentWorkflow) {
		this.parentWorkflow = parentWorkflow;
	}
	
}
