package com.projet.hetic.frag.mapper;

import com.projet.hetic.frag.dto.ChunkDTO;
import org.springframework.stereotype.Component;

import com.projet.hetic.frag.model.Chunk;

@Component
public class ChunkMapper {
  public Chunk bytesToEntity(byte[] bytes) {
    Chunk chunk = new Chunk();
    chunk.setSizeOriginal(bytes.length);
    return chunk;
  }

  public ChunkDTO toDto(Chunk chunk) {
    return new ChunkDTO(
            chunk.getId(),
            chunk.getHash(),
            chunk.getSizeOriginal(),
            chunk.getSizeCompressed()
    );
  }
}