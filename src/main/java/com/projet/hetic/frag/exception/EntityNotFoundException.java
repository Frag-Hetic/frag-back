package com.projet.hetic.frag.exception;

public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(String entityName, String field, String value) {
    super(String.format("%s not found with %s: %s", entityName, field, value));
  }
}
