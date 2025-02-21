package com.projet.hetic.frag.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ChunkingParamsDto {
  @PositiveOrZero(message = "La taille de la fenêtre ne peut pas être négative")
  private int windowSize = 48;
  @PositiveOrZero(message = "La taille minimale des chunks ne peut pas être négative")
  private int chunkMinSize = 1024;
  @PositiveOrZero(message = "La taille maximale des chunks ne peut pas être négative")
  private int chunkMaxSize = 8192;
  @Pattern(regexp = "^0x[0-9A-Fa-f]{1,4}$", message = "Le masque doit être au format hexadécimal (ex: 0x1FFF)", flags = Pattern.Flag.CASE_INSENSITIVE)
  private String breakpointMask = "0x1FFF";
}
