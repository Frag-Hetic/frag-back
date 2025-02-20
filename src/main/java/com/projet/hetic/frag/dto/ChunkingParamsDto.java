package com.projet.hetic.frag.dto;

import lombok.Data;

@Data
public class ChunkingParamsDto {
  private int windowSize = 48;
  private int chunkMinSize = 1024;
  private int chunkMaxSize = 8192;
  private String breakpointMask = "0x1FFF";
}
