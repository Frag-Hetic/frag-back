package com.projet.hetic.frag.controller;

import java.net.URI;
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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints")
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
  public ResponseEntity<UserOutputDto> createUser(@Valid @RequestBody UserInputDto userInputDto) {
    UserOutputDto createdUser = userService.createUser(userInputDto);
    URI location = URI.create(String.format("/users/%d", createdUser.getId()));
    return ResponseEntity.created(location).body(createdUser);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserOutputDto> updateUser(@PathVariable Long id,
      @Valid @RequestBody UserInputDto userInputDto) {
    return ResponseEntity.ok(userService.updateUser(id, userInputDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
  }

}
