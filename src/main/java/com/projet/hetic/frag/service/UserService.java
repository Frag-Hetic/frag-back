package com.projet.hetic.frag.service;

import java.util.List;
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

  public List<UserDto> getAllUsers() {
    return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
  }

  public Optional<UserDto> getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserMapper::toDTO);
  }
}