package com.projet.hetic.frag.mapper;

import org.springframework.stereotype.Component;

import com.projet.hetic.frag.model.Chunk;

@Component
public class ChunkMapper {
  public Chunk bytesToEntity(byte[] bytes) {
    Chunk chunk = new Chunk();
    chunk.setSizeOriginal(bytes.length);
    return chunk;
  }
}
