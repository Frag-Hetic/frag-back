package com.projet.hetic.frag.service;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.mapper.ChunkMapper;
import com.projet.hetic.frag.model.Chunk;

@Service
public class ChunkService {
  private final ChunkMapper chunkMapper;

  public ChunkService(ChunkMapper chunkMapper) {
    this.chunkMapper = chunkMapper;
  }

  public Chunk createChunk(byte[] bytes) {
    Chunk chunk = chunkMapper.bytesToEntity(bytes);

    // get compressed sized
    // get hash

    return chunk;
  }

}
