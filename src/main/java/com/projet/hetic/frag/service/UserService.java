package com.projet.hetic.frag.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.dto.UserDto;
import com.projet.hetic.frag.mapper.UserMapper;
import com.projet.hetic.frag.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<UserDto> getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserMapper::toDTO); // Convertit l'entité User en DTO
  }
}