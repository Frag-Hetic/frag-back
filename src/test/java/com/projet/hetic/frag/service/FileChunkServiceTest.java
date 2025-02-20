package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;
import com.projet.hetic.frag.repository.FileChunkRepository;

@ExtendWith(MockitoExtension.class)
class FileChunkServiceTest {

  @Mock
  private FileChunkRepository fileChunkRepository;

  private FileChunkService fileChunkService;

  @BeforeEach
  void setUp() {
    fileChunkService = new FileChunkService(fileChunkRepository);
  }

  @Test
  void createFileChunk_ShouldCreateAndReturnFileChunk() {
    // Arrange
    File file = new File();
    file.setId(1L);
    file.setFilename("test.txt");

    Chunk chunk = new Chunk();
    chunk.setId(1L);
    chunk.setData("test content".getBytes());

    int order = 1;
    long offsetStart = 100L;

    FileChunk expectedFileChunk = new FileChunk();
    expectedFileChunk.setFile(file);
    expectedFileChunk.setChunk(chunk);
    expectedFileChunk.setChunkOrder(order);
    expectedFileChunk.setOffsetStart(offsetStart);

    when(fileChunkRepository.save(any(FileChunk.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    FileChunk result = fileChunkService.createFileChunk(file, chunk, order, offsetStart);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getFile()).isEqualTo(file);
    assertThat(result.getChunk()).isEqualTo(chunk);
    assertThat(result.getChunkOrder()).isEqualTo(order);
    assertThat(result.getOffsetStart()).isEqualTo(offsetStart);

    verify(fileChunkRepository).save(any(FileChunk.class));
  }

  @Test
  void createFileChunk_ShouldNotAllowDuplicateFileIdAndChunkOrder() {
    // Arrange
    File file = new File();
    file.setId(1L);
    file.setFilename("test.txt");

    Chunk chunk1 = new Chunk();
    chunk1.setId(1L);
    chunk1.setData("content1".getBytes());

    Chunk chunk2 = new Chunk();
    chunk2.setId(2L);
    chunk2.setData("content2".getBytes());

    int order = 1;
    long offsetStart = 100L;

    when(fileChunkRepository.save(any(FileChunk.class)))
        .thenThrow(new DataIntegrityViolationException("Duplicate entry for file_id and chunk_order"));

    // Act & Assert
    assertThatThrownBy(() -> fileChunkService.createFileChunk(file, chunk1, order, offsetStart))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("Duplicate entry");
  }
}
