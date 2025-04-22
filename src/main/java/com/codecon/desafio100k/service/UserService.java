package com.codecon.desafio100k.service;

import com.codecon.desafio100k.model.ResponseTopCountries;
import com.codecon.desafio100k.model.User;
import com.codecon.desafio100k.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
                .stream().filter(this::isSuperUser)
                .toList();
    }

    public Map<String, ResponseTopCountries> getTopCountries() {
        HashMap<String, ResponseTopCountries> result = new HashMap<>();
        getAllUsers()
                .stream()
                .filter(this::isSuperUser)
                .forEach(user -> {
                    var pais = user.getPais();
                    if (result.containsKey(pais)) {
                        var response = result.get(pais);
                        response.getUsers().add(user);
                        response.setTotalUsers(response.getTotalUsers() + 1);
                    } else {
                        var newResponse = new ResponseTopCountries(1, new ArrayList<>());
                        newResponse.getUsers().add(user);
                        result.put(pais, newResponse);
                    }
                });

        result.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getTotalUsers(), e1.getValue().getTotalUsers()))
                .limit(5)
                .forEachOrdered(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    public void setUsers(List<User> newUsers) {
        userRepository.setUsers(newUsers);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    private boolean isSuperUser(User user) {
        return user.getScore() >= 900 && user.isAtivo();
    }
}
