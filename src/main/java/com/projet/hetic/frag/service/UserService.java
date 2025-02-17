package com.projet.hetic.frag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.dto.UserInputDto;
import com.projet.hetic.frag.dto.UserOutputDto;
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

  public List<UserOutputDto> getAllUsers() {
    return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
  }

  public UserOutputDto getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserMapper::toDTO)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  public UserOutputDto createUser(UserInputDto userInputDto) {
    User user = UserMapper.toEntity(userInputDto);
    User savedUser = userRepository.save(user);
    return UserMapper.toDTO(savedUser);
  }

  public UserOutputDto updateUser(Long id, UserInputDto userInputDto) {
    return userRepository.findById(id)
        .map(existingUser -> {
          existingUser.setName(userInputDto.getName());
          existingUser.setEmail(userInputDto.getEmail());
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