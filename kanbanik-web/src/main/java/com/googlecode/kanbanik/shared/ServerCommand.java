package com.googlecode.kanbanik.shared;

import java.io.Serializable;

public enum ServerCommand implements Serializable {
	NEW_PROJECT, GET_ALL_BOARDS_WITH_PROJECTS, MOVE_TASK, SAVE_TASK, DELETE_TASK
}
