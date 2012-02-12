package com.googlecode.kanbanik.server;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.googlecode.kanbanik.Board;
import com.googlecode.kanbanik.Kanbanik;
import com.googlecode.kanbanik.Project;
import com.googlecode.kanbanik.Task;
import com.googlecode.kanbanik.shared.KanbanikDTO;
import com.googlecode.kanbanik.shared.TaskDTO;

public class KanbanikServiceWorker {
	
	private Kanbanik kanbanik;
	
	private TaskBuilder taskBuilder;
	
	private BoardBuilder boardBuilder;
	
	@PersistenceContext
	private EntityManager manager;
	
	@Transactional
	public TaskDTO saveTask(TaskDTO taskDto) {
		Project project = kanbanik.getProjects().byId(taskDto.getProject().getId());
		Task storedTask = project.tasks().store(taskBuilder.build(taskDto), project, manager);
		
		return taskBuilder.build(storedTask, project);
	}
	
	@Transactional
	public void deleteTask(TaskDTO taskDto) {
		Project project = kanbanik.getProjects().byId(taskDto.getProject().getId());
		project.tasks().delete(taskBuilder.build(taskDto), manager);
	}
	
	@Transactional
	public KanbanikDTO fillKanbanik() {
		
		KanbanikDTO kanbanikDTO = new KanbanikDTO();
		for (Board board : kanbanik.getBoards().all()) {
			kanbanikDTO.addBoard(boardBuilder.build(board));
		}
		
		return kanbanikDTO;
		
	}

	public void setKanbanik(Kanbanik kanbanik) {
		this.kanbanik = kanbanik;
	}
	
	public void setTaskBuilder(TaskBuilder taskBuilder) {
		this.taskBuilder = taskBuilder;
	}

	public void setBoardBuilder(BoardBuilder boardBuilder) {
		this.boardBuilder = boardBuilder;
	}

}
