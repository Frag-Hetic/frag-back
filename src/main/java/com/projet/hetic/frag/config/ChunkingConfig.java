package com.projet.hetic.frag.config;

import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@Data
public class ChunkingConfig {

  public ChunkingConfig defaultChunkingConfig() {
    ChunkingConfig config = new ChunkingConfig();
    config.setWindowSize(48);
    config.setChunkMinSize(1024);
    config.setChunkMaxSize(8192);
    config.setBreakpointMask("0x1FFF");
    return config;
  }

  private int windowSize;
  private int chunkMinSize;
  private int chunkMaxSize;
  private String breakpointMask;
}
