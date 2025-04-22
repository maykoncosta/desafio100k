package com.codecon.desafio100k.service;

import com.codecon.desafio100k.model.User;
import com.codecon.desafio100k.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public void processarFile(MultipartFile file) throws IOException {
        List<User> users = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {});
        if (users.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não contém usuários válidos.");
        }
        this.setUsers(users);
    }

    public List<User> getSuperUsers() {
        return getAllUsers()
                .stream().filter(user -> user.getScore() >= 900 && user.isAtivo())
                .toList();
    }

    public void setUsers(List<User> newUsers) {
        userRepository.setUsers(newUsers);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

}
