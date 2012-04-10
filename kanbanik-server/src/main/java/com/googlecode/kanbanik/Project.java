package com.googlecode.kanbanik;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	@NamedQuery(
			name="allProjects",
			query="select p from Project p"
			)	
	})
public class Project implements Serializable {
	private static final long serialVersionUID = 8763753011402135932L;

	public static String ALL = "allProjects";
	
	@Id
	@GeneratedValue
	private int id;
	
	@ManyToMany
	@JoinTable(name="Board_Project", 
		joinColumns=@JoinColumn(name="PROJECTS_ID", unique=false, referencedColumnName="ID"),
		inverseJoinColumns=@JoinColumn(name="BOARD_ID", unique=false, referencedColumnName="ID")
	)
	private Collection<Board> board;
	
	private String name;

	@OneToMany
	private Collection<Task> tasks;
	
	public Collection<Board> getBoard() {
		return board;
	}
	
	public void addBoard(Board board) {
		if (board == null) {
			this.board = new ArrayList<Board>();
		}
		this.board.add(board);
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<Task> getTasks() {
		return tasks;
	}

	public Tasks tasks() {
		return new Tasks(this);
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
		Project other = (Project) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void removeTask(Task realTask) {
		getTasks().remove(realTask);
	}
}
