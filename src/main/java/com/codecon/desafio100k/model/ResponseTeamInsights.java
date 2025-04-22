package com.codecon.desafio100k.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseTeamInsights {
    private int totalMembros;
    private List<String> lideres;
    private List<String> projetosConcluidos;
    private double membrosAtivos;
}
