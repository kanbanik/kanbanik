package com.googlecode.kanbanik;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

public class Boards {
	
	private List<Board> boards;

	public Boards(List<Board> boards) {
		super();
		this.boards = boards;
	}
	
	public Board byId(int id) {
		for (Board board : boards) {
			if (board.getId() == id) {
				return board;
			}
		}
		
		throw new IllegalArgumentException("There is no such board with ID: '" + id + "'");
	}
	
	public List<Board> all() {
		return boards;
	}


	public ReturnObject delete(Board board, EntityManager manager) {
		Board realBoard = byId(board.getId());
		ReturnObject returnObject = checkProjects(realBoard);
		if (!returnObject.isOK()) {
			return returnObject;
		}
		
		returnObject = checkWorkflowitems(realBoard);
		if (!returnObject.isOK()) {
			return returnObject;
		}
		
		manager.remove(realBoard);
		return new ReturnObject(true, "");
	}

	private ReturnObject checkWorkflowitems(Board realBoard) {
		String msg = "There are the following workflowitems associated with this board. [";
		boolean isOk = true;
		for (Workflowitem item : realBoard.getWorkflow().getWorkflowitems()) {
			isOk = false;
			msg += item.getName() + ", ";
		}
		if (!isOk) {
			msg = msg.substring(0, msg.length() - 2);
			msg += "]. Please delete them first and than you can delete the board.";
			return new ReturnObject(false, msg);
		}
		
		return new ReturnObject(true, "");
	}

	private ReturnObject checkProjects(Board realBoard) {
		Collection<Project> projects = realBoard.getProjects().all();
		
		if (realBoard.getProjects().all().size() > 0) {
			String msg = "There are the following projects associated with this board. [";
			for (Project project: projects) {
				msg += project.getName() + ", ";
			}
			msg = msg.substring(0, msg.length() - 2);
			msg += "]. Please remove them from this board first and than you can delete the board.";
			return new ReturnObject(false, msg);
		}
		
		return new ReturnObject(true, "");
	}
	
	public Board store(Board board, EntityManager manager) {
		if (manager.find(Board.class, board.getId()) != null) {
			return editBoard(board, manager);
		} else {
			return storeNewBoard(board, manager);
		}
	}

	private Board editBoard(Board board, EntityManager manager) {
		Board realBoard = manager.find(Board.class, board.getId());
		realBoard.setName(board.getName());
		return realBoard;
	}

	private Board storeNewBoard(Board board, EntityManager manager) {
		Workflow workflow = new Workflow();
		manager.persist(workflow);
		
		board.setWorkflow(workflow);
		manager.persist(board);
		return board;
	}
}
