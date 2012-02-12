package com.googlecode.kanbanik;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class Kanbanik implements Serializable {

	private static final long serialVersionUID = 4755964690934201153L;

	private EntityManager manager;
	
	@PersistenceContext
	public void setManager(EntityManager manager) {
		this.manager = manager;
	}
	
	public Boards getBoards() {
		return new Boards(manager.createNamedQuery(Board.ALL, Board.class).getResultList());
	}
	
	public Projects getProjects() {
		return new Projects(manager.createNamedQuery(Project.ALL, Project.class).getResultList());
	}
}
