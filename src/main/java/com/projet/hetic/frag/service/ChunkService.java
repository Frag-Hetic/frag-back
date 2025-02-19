package com.projet.hetic.frag.service;

import com.projet.hetic.frag.dto.ChunkDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.projet.hetic.frag.mapper.ChunkMapper;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.repository.ChunkRepository;

import java.util.Optional;

@Service
public class ChunkService {
  private final ChunkMapper chunkMapper;
  private final CompressionService compressionService;
  private final HashingService hashingService;
  private final ChunkRepository chunkRepository;

  public ChunkService(ChunkMapper chunkMapper, CompressionService compressionService, HashingService hashingService,
      ChunkRepository chunkRepository) {
    this.chunkMapper = chunkMapper;
    this.compressionService = compressionService;
    this.hashingService = hashingService;
    this.chunkRepository = chunkRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Chunk findOrCreateChunk(byte[] bytes) {
    byte[] compressedBytes = compressionService.compressChunk(bytes);
    String hash = hashingService.hashAndCrypt64(compressedBytes);
    return chunkRepository.findByHash(hash)
        .orElseGet(() -> {
          Chunk chunk = chunkMapper.bytesToEntity(bytes);
          chunk.setData(compressedBytes);
          chunk.setSizeCompressed(compressedBytes.length);
          chunk.setCompressionType("ZLIB");
          chunk.setHash(hash);
          return chunkRepository.save(chunk);
        });
  }

    /**
     * Récupérer un chunk spécifique par son hash
     */
    public Optional<ChunkDTO> getChunkByHash(String hash) {
        return chunkRepository.findByHash(hash).map(chunkMapper::toDto);
    }

}
