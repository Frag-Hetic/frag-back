package com.projet.hetic.frag.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

@RestController
public class Healthy {
  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> getHealth() {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "OK");
    response.put("timestamp", System.currentTimeMillis());
    response.put("service", "frag-api");
    return ResponseEntity.ok(response);
  }
}
