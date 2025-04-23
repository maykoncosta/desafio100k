# 💡 Desafio Técnico: API para Performance e Análise de Dados

Este projeto foi desenvolvido como solução para o desafio técnico proposto pela [Codecon](https://github.com/codecon-dev/desafio-1-1s-vs-3j), que consiste em criar uma API performática para ingestão e análise de um grande volume de dados de usuários.

---

## ✅ Problema

Criar uma API REST que recebe um arquivo JSON com **100.000 usuários** e oferece **consultas analíticas rápidas (< 1s)** sobre os dados, como:
- Superusuários por país
- Insights sobre times e atividades
- Logins por dia
- Autoavaliação da performance da API

---

## 🧩 Solução

O projeto é **100% back-end**, utilizando:
- **Java 21** com **Spring Boot**
- Armazenamento em memória via `CopyOnWriteArrayList`
- Arquitetura em camadas
- Autoavaliação via chamadas internas com `RestTemplate`
- Medição de performance precisa com `System.nanoTime()`

---

## 🛠️ Decisões Técnicas e Arquitetura

| Escolha | Justificativa |
|--------|----------------|
| **Java + Spring Boot** | Robusto, familiar e com excelente desempenho |
| **In-Memory Storage** | Simples, rápido e suficiente para o escopo |
| **Thread-safe Collections** | Suporte a múltiplas requisições simultâneas |
| **Arquitetura em Camadas** | Clareza e facil manutenção|
| **Endpoint de Autoavaliação** | Permite validar rapidamente os critérios do desafio |

---

## 📂 Foco do Projeto

Este projeto é **back-end only**. Não possui interface gráfica. O foco foi total na performance da API e clareza do código.

---

## 🧪 Como usar

### Requisitos
- Java 21+
- Maven

### Execução

```bash
git clone https://github.com/maykoncosta/desafio100k.git
cd desafio100k
./mvnw spring-boot:run
