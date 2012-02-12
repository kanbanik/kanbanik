package com.googlecode.kanbanik;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="workflowitem_type")
public abstract class Workflowitem implements Serializable {
	
	private static final long serialVersionUID = 3653467755228812383L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(nullable=true)
	private Integer wipLimit;
	
	@Column(name="name")
	private String name;
	
	@OneToOne
	private Workflowitem nextItem;
	
	@OneToMany
    private Collection<Task> tasks;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Workflowitem getNextItem() {
		return nextItem;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setNextItem(Workflowitem nextItem) {
		this.nextItem = nextItem;
	}

	public Collection<Task> getTasks() {
        return tasks;
	}
	
	public Integer getWipLimit() {
		if (wipLimit == null) {
			return -1;
		}
		
		return wipLimit;
	}

	public void setWipLimit(Integer wipLimit) {
		this.wipLimit = wipLimit;
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
		Workflowitem other = (Workflowitem) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void removeTask(Task realTask) {
		getTasks().remove(realTask);
	}

}
