package com.googlecode.kanbanik;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({
	@NamedQuery(
			name="allBoards",
			query="select b from Board b"
			)	
	})
public class Board implements Serializable {
	
	private static final long serialVersionUID = -3113666438422963568L;

	public static String ALL = "allBoards";
	
	@Id
	@GeneratedValue
	private int id;
	
	private String name;
	
	@OneToOne(cascade=CascadeType.REMOVE)
	private Workflow workflow;
	
	@OneToMany
	private Collection<Project> projects;
	
	public Workflow getWorkflow() {
		return workflow;
	}
	
	public String getName() {
		return name;
	}

	public Projects getProjects() {
		return new Projects(projects);
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void addProject(Project project) {
		if (projects == null) {
			projects = new ArrayList<Project>();
		}
		
		projects.add(project);
	}
	
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void removeProject(Project project) {
		if (projects == null) {
			throw new IllegalArgumentException("Not possible to remove project with id " + project.getId() + " because there are no projects");
		}
		
		projects.remove(project);
	}
}
