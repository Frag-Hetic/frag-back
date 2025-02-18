package com.projet.hetic.frag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileIntputDto {
  private String filename;
  private Long fileSize;
  private String mimeType;
  private String checkhash;
}
