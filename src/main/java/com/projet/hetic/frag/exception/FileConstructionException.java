package com.projet.hetic.frag.exception;

public class FileConstructionException extends RuntimeException {
  public FileConstructionException(Long fileId, String error) {
    super(String.format("Error reconstructing file %d: %s", fileId, error));
  }
}
