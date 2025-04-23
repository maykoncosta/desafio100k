package com.codecon.desafio100k.service;

import com.codecon.desafio100k.model.ResponseTeamInsights;
import com.codecon.desafio100k.model.User;
import com.codecon.desafio100k.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public void processarFile(MultipartFile file) throws IOException {
        List<User> users = objectMapper.readValue(file.getInputStream(), new TypeReference<>() {
        });
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

    public Map<String, Integer> getTopCountries() {
        HashMap<String, Integer> result = new HashMap<>();
        getAllUsers()
                .stream()
                .filter(this::isSuperUser)
                .forEach(user -> {
                    var pais = user.getPais();
                    if (result.containsKey(pais)) {
                        var response = result.get(pais);
                        result.put(pais, ++response);
                    } else {
                        result.put(pais, 1);
                    }
                });

        return result.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, ResponseTeamInsights> getTeamInsights() {
        Map<String, ResponseTeamInsights> result = new HashMap<>();
        getAllUsers()
                .stream()
                .forEach(user -> {
                    var equipe = user.getEquipe();
                    if (result.containsKey(equipe.getNome())) {
                        var response = result.get(equipe.getNome());
                        response.setTotalMembros(response.getTotalMembros() + 1);
                        if (equipe.isLider()) {
                            if (response.getLideres() != null) {
                                response.getLideres().add(user.getNome());
                            } else {
                                response.setLideres(new ArrayList<>());
                                response.getLideres().add(user.getNome());
                            }
                        }
                        if (user.isAtivo()) {
                            response.setMembrosAtivos(response.getMembrosAtivos() + 1);
                        }
                        var projetos = equipe.getProjetos();
                        if (projetos != null) {
                            projetos.forEach(projeto -> {
                                if (projeto.concluido()) {
                                    if (response.getProjetosConcluidos() != null) {
                                        response.getProjetosConcluidos().add(projeto.nome());
                                    } else {
                                        response.setProjetosConcluidos(new ArrayList<>());
                                        response.getProjetosConcluidos().add(projeto.nome());
                                    }
                                }
                            });
                        }
                    } else {
                        var response = new ResponseTeamInsights();
                        response.setTotalMembros(1);
                        if (user.isAtivo()) {
                            response.setMembrosAtivos(1);
                        }
                        response.setLideres(new ArrayList<>());
                        response.getLideres().add(user.getNome());
                        var projetos = equipe.getProjetos();
                        if (projetos != null) {
                            projetos.forEach(projeto -> {
                                if (projeto.concluido()) {
                                    if (response.getProjetosConcluidos() != null) {
                                        response.getProjetosConcluidos().add(projeto.nome());
                                    } else {
                                        response.setProjetosConcluidos(new ArrayList<>());
                                        response.getProjetosConcluidos().add(projeto.nome());
                                    }
                                }
                            });
                        }
                        result.put(equipe.getNome(), response);
                    }
                });

        result.entrySet().forEach(entry -> {
            var response = entry.getValue();
            response.setMembrosAtivos((response.getMembrosAtivos() * 100) / response.getTotalMembros());
            response.setMembrosAtivos(Math.round(response.getMembrosAtivos() * 100.0) / 100.0);
        });
        return result;
    }

    public Map<LocalDate, Integer> getActiversUsersPerDay(Long min) {
        Map<LocalDate, Integer> result = new HashMap<>();
        getAllUsers()
                .stream()
                .filter(User::isAtivo)
                .forEach(user ->
                        user.getLogs().forEach(log -> {
                            var data = log.data();
                            if (result.containsKey(data)) {
                                var response = result.get(data);
                                result.put(data, ++response);
                            } else {
                                result.put(data, 1);
                            }
                        })
                );

        Long minOcorrencias = min == null ? 0 : min;
        return result.entrySet().stream()
                .filter(entry -> entry.getValue() >= minOcorrencias)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
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
