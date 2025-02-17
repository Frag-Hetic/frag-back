package com.projet.hetic.frag.mapper;

import com.projet.hetic.frag.dto.UserDto;
import com.projet.hetic.frag.model.User;

public class UserMapper {
  public static UserDto toDTO(User user) {
    return new UserDto(user.getId(), user.getName(), user.getEmail());
  }

  public static User toEntity(UserDto userDTO) {
    User user = new User();
    user.setId(userDTO.getId());
    user.setName(userDTO.getName());
    user.setEmail(userDTO.getEmail());
    return user;
  }
}
