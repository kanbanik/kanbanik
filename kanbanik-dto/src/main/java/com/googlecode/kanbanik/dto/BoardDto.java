package com.googlecode.kanbanik.dto;

import java.util.ArrayList;
import java.util.List;

public class BoardDto implements IdentifiableDto {

	private static final long serialVersionUID = -4409696591604175858L;

	private String name;
	
	private WorkfloVerticalSizing workfloVerticalSizing;
	
	private String id;
	
	private int version;
	
	private WorkflowDto workflow;
	
	private int verticalSizingFixedSize;
	
	private boolean showUserPictureEnabled;
	
	private List<TaskDto> tasks = new ArrayList<TaskDto>();
	
	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public WorkfloVerticalSizing getWorkfloVerticalSizing() {
		return workfloVerticalSizing;
	}

	public void setWorkfloVerticalSizing(WorkfloVerticalSizing workfloVerticalSizing) {
		this.workfloVerticalSizing = workfloVerticalSizing;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public WorkflowDto getWorkflow() {
		return workflow;
	}

	public void setWorkflow(WorkflowDto workflow) {
		this.workflow = workflow;
	}
	
	public List<TaskDto> getTasks() {
		return tasks;
	}

	public void setTasks(List<TaskDto> tasks) {
		this.tasks = tasks;
	}
	
	public int getVerticalSizingFixedSize() {
		return verticalSizingFixedSize;
	}

	public void setVerticalSizingFixedSize(int verticalSizingFixedSize) {
		this.verticalSizingFixedSize = verticalSizingFixedSize;
	}

	public boolean isShowUserPictureEnabled() {
		return showUserPictureEnabled;
	}

	public void setShowUserPictureEnabled(boolean showUserPictureEnabled) {
		this.showUserPictureEnabled = showUserPictureEnabled;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardDto other = (BoardDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
