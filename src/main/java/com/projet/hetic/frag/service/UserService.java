package com.projet.hetic.frag.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<UserDTO> getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserMapper::toDTO); // Convertit l'entité User en DTO
  }
}