package com.projet.hetic.frag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.dto.UserInputDto;
import com.projet.hetic.frag.dto.UserOutputDto;
import com.projet.hetic.frag.exception.EntityNotFoundException;
import com.projet.hetic.frag.mapper.UserMapper;
import com.projet.hetic.frag.model.User;
import com.projet.hetic.frag.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  public List<UserOutputDto> getAllUsers() {
    return userRepository.findAll().stream().map(userMapper::toDTO).toList();
  }

  public UserOutputDto getUserById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new EntityNotFoundException("User", "id", id.toString()));
  }

  public UserOutputDto createUser(UserInputDto userInputDto) {
    User user = userMapper.toEntity(userInputDto);
    User savedUser = userRepository.save(user);
    return userMapper.toDTO(savedUser);
  }

  public UserOutputDto updateUser(Long id, UserInputDto userInputDto) {
    return userRepository.findById(id)
        .map(existingUser -> {
          existingUser.setName(userInputDto.getName());
          existingUser.setEmail(userInputDto.getEmail());
          return userMapper.toDTO(userRepository.save(existingUser));
        })
        .orElseThrow(() -> new EntityNotFoundException("User", "id", id.toString()));
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException("User", "id", id.toString());
    }
    userRepository.deleteById(id);
  }
}