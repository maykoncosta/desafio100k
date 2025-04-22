package com.codecon.desafio100k.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseTopCountries {
    int totalUsers;
    List<User> users;

    public ResponseTopCountries(int i, List<User> users) {
        this.totalUsers = i;
        this.users = users;
    }
}
