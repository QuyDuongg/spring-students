package com.example.studentapp.controller;

import com.example.studentapp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    
    @Autowired(required = false)
    private StudentRepository studentRepository;
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student App API is running");
        response.put("status", "OK");
        response.put("database", "Azure PostgreSQL - Connected");
        response.put("endpoints", Map.of(
            "GET /", "Home page (this page)",
            "GET /health", "Health check",
            "GET /test-db", "Test database connection",
            "GET /students", "Get all students",
            "GET /students/{id}", "Get student by ID",
            "POST /students", "Create a new student"
        ));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-db")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            if (studentRepository != null) {
                long count = studentRepository.count();
                response.put("status", "SUCCESS");
                response.put("message", "Database connection is working!");
                response.put("totalStudents", count);
            } else {
                response.put("status", "ERROR");
                response.put("message", "StudentRepository is not available");
            }
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Database connection failed: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}

