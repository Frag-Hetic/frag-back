package com.projet.hetic.frag.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
  private Long id;
  private String name;
  private String email;
}
