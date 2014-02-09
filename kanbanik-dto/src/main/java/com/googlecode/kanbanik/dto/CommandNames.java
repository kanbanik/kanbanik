package com.googlecode.kanbanik.dto;

public enum CommandNames {

    LOGIN("login"),
    LOGOUT("logout"),

    // user
    GET_ALL_USERS_COMMAND("getAllUsers"),
    GET_CURRENT_USER("getCurrentUser"),
    CREATE_USER("createUser"),
    EDIT_USER("editUser"),
    DELETE_USER("deleteUser"),

    // class of service
    GET_ALL_CLASS_OF_SERVICE("getAllClassOfServices"),
    EDIT_CLASS_OF_SERVICE("editClassOfService"),
    CREATE_CLASS_OF_SERVICE("createClassOfService"),
    DELETE_CLASS_OF_SERVICE("deleteClassOfService"),

    // project
    GET_ALL_PROJECTS("getAllProjects"),
    EDIT_PROJECT("editProject"),
    CREATE_PROJECT("createProject"),
    DELETE_PROJECT("deleteProject"),
    ADD_PROJECT_TO_BOARD("addProjectToBoard"),
    REMOVE_PROJECT_FROM_BOARD("removeProjectFromBoard"),

    // task
    MOVE_TASK("moveTask"),
    CREATE_TASK("createTask"),
    EDIT_TASK("editTask"),
    GET_TASK("getTask"),
    DELETE_TASK("deleteTask")
    ;

    public String name;

    private CommandNames(String name) {
        this.name = name;
    }



}
