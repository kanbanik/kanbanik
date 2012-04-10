package com.googlecode.kanbanik;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

@Entity
public class Task implements Serializable {
	
	private static final long serialVersionUID = 1924518700066386066L;

	@Id
	@GeneratedValue
	private Integer id;
	
	private String name;
	
	@Column(name="ticket_id")
	private String ticketId;
	
	@ManyToOne
	@JoinTable(name="Workflowitem_Task", 
		joinColumns=@JoinColumn(name="TASKS_ID", unique=false, referencedColumnName="ID"),
		inverseJoinColumns=@JoinColumn(name="WORKFLOWITEM_ID", unique=false, referencedColumnName="ID")
	)
	private Workflowitem workflowitem;

	private String description;
	
	@Enumerated
	private ClassOfService classOfService;
	
	public String getName() {
		return name;
	}

	public String getTicketId() {
		return ticketId;
	}

	public Workflowitem getWorkflowitem() {
		return workflowitem;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWorkflowitem(Workflowitem workflowitem) {
		this.workflowitem = workflowitem;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ClassOfService getClassOfService() {
		return classOfService;
	}

	public void setClassOfService(ClassOfService classOfService) {
		this.classOfService = classOfService;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
}
