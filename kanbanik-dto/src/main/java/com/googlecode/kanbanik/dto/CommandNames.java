package com.googlecode.kanbanik.dto;

public enum CommandNames {

    LOGIN("login"),
    LOGOUT("logout"),
    GET_CURRENT_USER("getCurrentUser");

    public String name;

    private CommandNames(String name) {
        this.name = name;
    }



}
