package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.FileChunk;

@ExtendWith(MockitoExtension.class)
class FileConstructionServiceTest {

  @Mock
  private CompressionService compressionService;

  @InjectMocks
  private FileConstructionService fileConstructionService;

  private Chunk chunk1;
  private Chunk chunk2;
  private FileChunk fileChunk1;
  private FileChunk fileChunk2;

  @BeforeEach
  void setUp() {
    // Préparation des données de test
    chunk1 = new Chunk();
    chunk1.setData("compressed1".getBytes());

    chunk2 = new Chunk();
    chunk2.setData("compressed2".getBytes());

    fileChunk1 = new FileChunk();
    fileChunk1.setChunk(chunk1);

    fileChunk2 = new FileChunk();
    fileChunk2.setChunk(chunk2);
  }

  @Test
  void reconstructFileFromChunks_Success() {
    // Arrange
    List<FileChunk> fileChunks = Arrays.asList(fileChunk1, fileChunk2);
    when(compressionService.decompressChunk(chunk1.getData()))
        .thenReturn("uncompressed1".getBytes());
    when(compressionService.decompressChunk(chunk2.getData()))
        .thenReturn("uncompressed2".getBytes());

    // Act
    byte[] result = fileConstructionService.reconstructFileFromChunks(fileChunks, 1L);

    // Assert
    assertThat(result)
        .isNotNull()
        .isEqualTo("uncompressed1uncompressed2".getBytes());
  }

  @Test
  void reconstructFileFromChunks_EmptyChunksList() {
    // Arrange
    List<FileChunk> fileChunks = Collections.emptyList();

    // Act
    byte[] result = fileConstructionService.reconstructFileFromChunks(fileChunks, 1L);

    // Assert
    assertThat(result)
        .isNotNull()
        .isEmpty();
  }

  @Test
  void reconstructFileFromChunks_CompressionServiceFailure() {
    // Arrange
    List<FileChunk> fileChunks = Arrays.asList(fileChunk1);
    when(compressionService.decompressChunk(chunk1.getData()))
        .thenThrow(new RuntimeException("Decompression failed"));

    // Act & Assert
    assertThatThrownBy(() -> fileConstructionService.reconstructFileFromChunks(fileChunks, 1L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Decompression failed");
  }
}