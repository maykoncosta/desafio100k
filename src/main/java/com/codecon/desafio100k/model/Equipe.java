package com.codecon.desafio100k.model;

import lombok.Data;

import java.util.List;

@Data
public class Equipe {
    private String nome;
    private boolean lider;
    private List<Projeto> projetos;
}