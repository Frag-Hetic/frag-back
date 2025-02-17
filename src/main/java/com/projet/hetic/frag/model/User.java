package com.projet.hetic.frag.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class User {
  @Id
  @GeneratedValue
  private Long id;
  private String name;
  private String email;
}
