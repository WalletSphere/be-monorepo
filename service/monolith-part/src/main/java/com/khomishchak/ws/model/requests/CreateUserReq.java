package com.khomishchak.ws.model.requests;

public record CreateUserReq(String username, String password, String email, boolean acceptTC) {
}
