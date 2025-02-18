package com.projet.hetic.frag.mapper;

import org.springframework.stereotype.Component;

import com.projet.hetic.frag.dto.UserInputDto;
import com.projet.hetic.frag.dto.UserOutputDto;
import com.projet.hetic.frag.model.User;

@Component
public class UserMapper {
  public UserOutputDto toDTO(User user) {
    return new UserOutputDto(user.getId(), user.getName(), user.getEmail());
  }

  public User toEntity(UserInputDto userInputDTO) {
    User user = new User();
    user.setName(userInputDTO.getName());
    user.setEmail(userInputDTO.getEmail());
    return user;
  }
}
