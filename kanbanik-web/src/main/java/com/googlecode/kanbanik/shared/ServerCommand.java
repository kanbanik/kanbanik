package com.googlecode.kanbanik.shared;

import java.io.Serializable;

public enum ServerCommand implements Serializable {
	GET_ALL_BOARDS_WITH_PROJECTS,
	GET_ALL_PROJECTS,
	MOVE_TASK, 
	SAVE_PROJECT, 
	SAVE_TASK, 
	SAVE_BOARD,
	GET_BOARD,
	DELETE_TASK,
	DELETE_BOARD,
	DELETE_PROJECT,
	ADD_PROJECTS_TO_BOARD,
	REMOVE_PROJECTS_FROM_BOARD,
	EDIT_WORKFLOW
	
}
