package com.codecon.desafio100k.repository;

import com.codecon.desafio100k.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class UserRepository {

    private final List<User> users = new CopyOnWriteArrayList<User>();

    public void setUsers(List<User> newUsers) {
        users.clear();
        users.addAll(newUsers);
    }

    public List<User> getAllUsers() {
        return users;
    }
}
