package com.khomishchak.goalsservice.model;

public enum CustomHeader {
    USER_ID("UserId");

    private String value;

    CustomHeader(String value) {
        this.value = value;
    }
}
