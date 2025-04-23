package com.codecon.desafio100k.model;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String nome;
    private int idade;
    private int score;
    private boolean ativo;
    private String pais;
    private Equipe equipe;
    private List<Logs> logs;
}
