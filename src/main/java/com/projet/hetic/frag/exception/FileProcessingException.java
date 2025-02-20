package com.projet.hetic.frag.exception;

public class FileProcessingException extends RuntimeException {
  public FileProcessingException(String error) {
    super(String.format("Error processing file: %s", error));
  }

}
