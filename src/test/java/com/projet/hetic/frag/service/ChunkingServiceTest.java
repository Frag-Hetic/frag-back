package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChunkingServiceTest {

  private ChunkingService chunkingService;
  private static final int CHUNK_SIZE = 1024 * 8;

  @BeforeEach
  void setUp() {
    chunkingService = new ChunkingService();
  }

  @Test
  void chunkFile_ShouldReturnEmptyStream_WhenInputIsEmpty() {
    // Arrange
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

    // Act
    List<byte[]> chunks = chunkingService.chunkFile(emptyStream)
        .collect(Collectors.toList());

    // Assert
    assertThat(chunks).isEmpty();
  }

  @Test
  void chunkFile_ShouldCreateSingleChunk_WhenDataSmallerThanChunkSize() {
    // Arrange
    byte[] smallData = "Test data".getBytes();
    InputStream inputStream = new ByteArrayInputStream(smallData);

    // Act
    List<byte[]> chunks = chunkingService.chunkFile(inputStream)
        .collect(Collectors.toList());

    // Assert
    assertThat(chunks).hasSize(1);
    assertThat(chunks.get(0)).isEqualTo(smallData);
  }

  @Test
  void chunkFile_ShouldCreateMultipleChunks_WhenDataLargerThanChunkSize() {
    // Arrange
    byte[] largeData = new byte[CHUNK_SIZE * 2 + 100];
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) (i % 256);
    }
    InputStream inputStream = new ByteArrayInputStream(largeData);

    // Act
    List<byte[]> chunks = chunkingService.chunkFile(inputStream)
        .collect(Collectors.toList());

    // Assert
    assertThat(chunks).hasSizeGreaterThan(1);

    // Afficher les informations de debug
    System.out.println("Nombre de chunks : " + chunks.size());
    for (int i = 0; i < chunks.size(); i++) {
      System.out.println("Taille du chunk " + i + " : " + chunks.get(i).length);
    }

    int totalSize = chunks.stream()
        .mapToInt(chunk -> chunk.length)
        .sum();
    System.out.println("Taille totale : " + totalSize);
    System.out.println("Taille attendue : " + largeData.length);

    assertThat(totalSize).isEqualTo(largeData.length);
  }

  @Test
  void chunkFile_ShouldCreateConsistentChunks_WhenProcessingSameDataTwice() {
    // Arrange
    byte[] testData = "Test data for consistent chunking".getBytes();
    InputStream firstStream = new ByteArrayInputStream(testData);
    InputStream secondStream = new ByteArrayInputStream(testData);

    // Act
    List<byte[]> firstChunks = chunkingService.chunkFile(firstStream)
        .collect(Collectors.toList());
    List<byte[]> secondChunks = chunkingService.chunkFile(secondStream)
        .collect(Collectors.toList());

    // Assert
    assertThat(firstChunks).hasSameSizeAs(secondChunks);
    for (int i = 0; i < firstChunks.size(); i++) {
      assertThat(firstChunks.get(i)).isEqualTo(secondChunks.get(i));
    }
  }
}