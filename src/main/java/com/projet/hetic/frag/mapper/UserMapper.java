package com.projet.hetic.frag.mapper;

import com.projet.hetic.frag.dto.UserInputDto;
import com.projet.hetic.frag.dto.UserOutputDto;
import com.projet.hetic.frag.model.User;

public class UserMapper {
  public static UserOutputDto toDTO(User user) {
    return new UserOutputDto(user.getId(), user.getName(), user.getEmail());
  }

  public static User toEntity(UserInputDto userInputDTO) {
    User user = new User();
    user.setName(userInputDTO.getName());
    user.setEmail(userInputDTO.getEmail());
    return user;
  }
}
