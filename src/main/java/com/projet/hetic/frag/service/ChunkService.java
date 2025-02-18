package com.projet.hetic.frag.service;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.mapper.ChunkMapper;
import com.projet.hetic.frag.model.Chunk;

@Service
public class ChunkService {
  private final ChunkMapper chunkMapper;
  private final CompressionService compressionService;
  private final HashingService hashingService;

  public ChunkService(ChunkMapper chunkMapper, CompressionService compressionService, HashingService hashingService) {
    this.chunkMapper = chunkMapper;
    this.compressionService = compressionService;
    this.hashingService = hashingService;
  }

  public Chunk createChunk(byte[] bytes) {
    Chunk chunk = chunkMapper.bytesToEntity(bytes);

    byte[] compressedBytes = compressionService.compressChunk(bytes);
    chunk.setData(compressedBytes);
    chunk.setSizeCompressed(compressedBytes.length);
    chunk.setCompressionType("ZLIB");

    String hash = hashingService.calculateSHA256(compressedBytes);
    chunk.setHash(hash);

    return chunk;
  }

}
