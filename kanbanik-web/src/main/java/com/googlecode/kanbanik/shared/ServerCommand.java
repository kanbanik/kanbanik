package com.googlecode.kanbanik.shared;

import java.io.Serializable;

public enum ServerCommand implements Serializable {
	NEW_PROJECT, 
	GET_ALL_BOARDS_WITH_PROJECTS,
	GET_ALL_PROJECTS,
	MOVE_TASK, 
	SAVE_TASK, 
	SAVE_BOARD,
	DELETE_TASK,
	DELETE_BOARD
}
