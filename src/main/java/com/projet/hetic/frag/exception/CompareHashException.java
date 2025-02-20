package com.projet.hetic.frag.exception;

public class CompareHashException extends RuntimeException {
  public CompareHashException() {
    super(String.format("Error file content hash mismatch"));
  }
}
