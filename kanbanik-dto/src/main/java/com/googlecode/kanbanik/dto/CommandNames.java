package com.googlecode.kanbanik.dto;

public enum CommandNames {

    LOGIN("login"),
    LOGOUT("logout"),
    GET_CURRENT_USER("getCurrentUser"),
    CREATE_USER("createUser"),
    EDIT_USER("editUser"),
    DELETE_USER("deleteUser"),
    GET_ALL_USERS_COMMAND("getAllUsers")
    ;

    public String name;

    private CommandNames(String name) {
        this.name = name;
    }



}
