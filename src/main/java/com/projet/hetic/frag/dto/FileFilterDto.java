package com.projet.hetic.frag.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FileFilterDto {
  @Size(max = 255)
  private String fileName;
  @Size(max = 100)
  private String mimeType;
}