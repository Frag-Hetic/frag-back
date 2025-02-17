package com.projet.hetic.frag.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.hetic.frag.dto.UserInputDto;
import com.projet.hetic.frag.dto.UserOutputDto;
import com.projet.hetic.frag.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserOutputDto>> getUsers() {
    List<UserOutputDto> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserOutputDto> getUserById(@PathVariable Long id) {
    return ResponseEntity.ok(
        userService.getUserById(id));
  }

  @PostMapping
  public ResponseEntity<UserOutputDto> createUser(@RequestBody UserInputDto userInputDto) {
    return ResponseEntity.ok(userService.createUser(userInputDto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserOutputDto> updateUser(@PathVariable Long id, @RequestBody UserInputDto userInputDto) {
    return ResponseEntity.ok(userService.updateUser(id, userInputDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
  }

}
