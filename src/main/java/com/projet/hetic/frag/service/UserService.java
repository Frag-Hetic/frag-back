package com.projet.hetic.frag.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.dto.UserDto;
import com.projet.hetic.frag.exception.UserNotFoundException;
import com.projet.hetic.frag.mapper.UserMapper;
import com.projet.hetic.frag.model.User;
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

  public UserDto createUser(UserDto userDto) {
    User user = UserMapper.toEntity(userDto);
    User savedUser = userRepository.save(user);
    return UserMapper.toDTO(savedUser);
  }

  public UserDto updateUser(Long id, UserDto userDto) {
    return userRepository.findById(id)
        .map(existingUser -> {
          existingUser.setName(userDto.getName());
          existingUser.setEmail(userDto.getEmail());
          return UserMapper.toDTO(userRepository.save(existingUser));
        })
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }
    userRepository.deleteById(id);
  }
}