package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.projet.hetic.frag.mapper.ChunkMapper;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.repository.ChunkRepository;

@ExtendWith(MockitoExtension.class)
class ChunkServiceTest {

  @Mock
  private ChunkMapper chunkMapper;
  @Mock
  private CompressionService compressionService;
  @Mock
  private HashingService hashingService;
  @Mock
  private ChunkRepository chunkRepository;

  private ChunkService chunkService;
  private byte[] testBytes;
  private byte[] compressedBytes;
  private String testHash;

  @BeforeEach
  void setUp() {
    chunkService = new ChunkService(chunkMapper, compressionService, hashingService, chunkRepository);
    testBytes = "test data".getBytes();
    compressedBytes = "compressed data".getBytes();
    testHash = "test-hash-123";
  }

  @Test
  void findOrCreateChunk_WhenChunkExists_ShouldReturnExistingChunk() {
    // Arrange
    Chunk existingChunk = new Chunk();
    when(compressionService.compressChunk(testBytes)).thenReturn(compressedBytes);
    when(hashingService.hashAndCrypt64(compressedBytes)).thenReturn(testHash);
    when(chunkRepository.findByHash(testHash)).thenReturn(Optional.of(existingChunk));

    // Act
    Chunk result = chunkService.findOrCreateChunk(testBytes);

    // Assert
    assertThat(result).isEqualTo(existingChunk);
    verify(chunkMapper, never()).bytesToEntity(any());
    verify(chunkRepository, never()).save(any());
  }

  @Test
  void findOrCreateChunk_WhenChunkDoesNotExist_ShouldCreateAndSaveNewChunk() {
    // Arrange
    Chunk newChunk = new Chunk();
    when(compressionService.compressChunk(testBytes)).thenReturn(compressedBytes);
    when(hashingService.hashAndCrypt64(compressedBytes)).thenReturn(testHash);
    when(chunkRepository.findByHash(testHash)).thenReturn(Optional.empty());
    when(chunkMapper.bytesToEntity(testBytes)).thenReturn(newChunk);
    when(chunkRepository.save(any(Chunk.class))).thenReturn(newChunk);

    // Act
    Chunk result = chunkService.findOrCreateChunk(testBytes);

    // Assert
    assertThat(result).isEqualTo(newChunk);
    assertThat(result.getData()).isEqualTo(compressedBytes);
    assertThat(result.getSizeCompressed()).isEqualTo(compressedBytes.length);
    assertThat(result.getCompressionType()).isEqualTo("ZLIB");
    assertThat(result.getHash()).isEqualTo(testHash);

    verify(chunkMapper).bytesToEntity(testBytes);
    verify(chunkRepository).save(newChunk);
  }
}
