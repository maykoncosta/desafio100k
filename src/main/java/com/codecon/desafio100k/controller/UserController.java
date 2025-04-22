package com.codecon.desafio100k.controller;

import com.codecon.desafio100k.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = {"multipart/form-data"}, produces = {"application/json"})
    public ResponseEntity<Map<String, Object>> uploadUsers(@RequestPart("file") MultipartFile file) throws IOException {
        long start = System.nanoTime();
        log.info("File name: {}", file.getOriginalFilename());
        log.info("File size: {}", file.getSize()/1000);
        userService.processarFile(file);
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        log.info("Elapsed time: {} ms", elapsedMs);
        return ResponseEntity.ok(Map.of(
                "timestamp", Instant.now(),
                "status", "success",
                "message", "File processed successfully",
                "elapsedTime", elapsedMs));
    }

    @GetMapping("/superusers")
    public ResponseEntity<Map<String, Object>> getSuperusers() {
        long start = System.nanoTime();
        log.info("Fetching superusers");
        var superusers = userService.getSuperUsers();
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        log.info("Elapsed time: {} ms", elapsedMs);
        return ResponseEntity.ok(Map.of(
                "data", superusers,
                "total", superusers.size(),
                "timestamp", Instant.now(),
                "status", "success",
                "elapsedTime", elapsedMs
                ));
    }

}
