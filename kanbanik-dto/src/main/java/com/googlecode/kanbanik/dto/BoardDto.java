package com.googlecode.kanbanik.dto;



public class BoardDto implements KanbanikDto {

	private static final long serialVersionUID = -4409696591604175858L;

	private String name;
	
	private String id;
	
	private int version;

	private WorkflowitemDto rootWorkflowitem;
	
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

	public WorkflowitemDto getRootWorkflowitem() {
		return rootWorkflowitem;
	}

	public void setRootWorkflowitem(WorkflowitemDto rootWorkflowitem) {
		this.rootWorkflowitem = rootWorkflowitem;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		BoardDto other = (BoardDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
