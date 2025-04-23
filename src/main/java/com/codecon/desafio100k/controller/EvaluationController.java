package com.codecon.desafio100k.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/evaluation")
public class EvaluationController {

    private RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    private final List<String> endpoints = List.of(
            "/users/superusers",
            "/users/top-countries",
            "/users/team-insights",
            "/users/active-users-per-day"
    );

    public EvaluationController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> evaluate(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString().replace("/evaluation", "");
        List<Map<String, Object>> results = new ArrayList<>();

        boolean allOk = true;
        long totalTime = 0;

        for (String endpoint : endpoints) {
            long start = System.nanoTime();
            Map<String, Object> result = new HashMap<>();
            try {
                var url = baseUrl + endpoint;
                var response = restTemplate.getForEntity(url, String.class);
                long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

                boolean isValidJson = isValidJson(response.getBody().toString());

                result.put("endpoint", endpoint);
                result.put("status", response.getStatusCodeValue());
                result.put("timeMs", duration);
                result.put("validJson", isValidJson);

                if (response.getStatusCodeValue() != 200 || !isValidJson || duration > 1000) {
                    allOk = false;
                }

                totalTime += duration;
            } catch (Exception e) {
                result.put("endpoint", endpoint);
                result.put("status", "ERROR");
                result.put("timeMs", -1);
                result.put("validJson", false);
                allOk = false;
            }
            results.add(result);
        }

        String score = allOk ? "Ok" : "Not Ok";

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now().toString());
        response.put("evaluation", results);
        response.put("averageTimeMs", totalTime / Math.max(results.size(), 1));
        response.put("score", score);

        return ResponseEntity.ok(response);
    }

    private boolean isValidJson(String body) {
        try {
            objectMapper.readTree(body);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
