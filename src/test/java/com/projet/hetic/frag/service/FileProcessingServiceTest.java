package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.File;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

  @Mock
  private FileService fileService;
  @Mock
  private ChunkingService chunkingService;
  @Mock
  private ChunkService chunkService;
  @Mock
  private FileChunkService fileChunkService;
  @Mock
  private CompressionService compressionService;
  @Mock
  private HashingService hashingService;

  private FileProcessingService fileProcessingService;

  @BeforeEach
  void setUp() {
    fileProcessingService = new FileProcessingService(
        fileService,
        chunkingService,
        chunkService,
        fileChunkService, compressionService, hashingService);
  }

  @Test
  void processAndSplitFile_ShouldProcessSuccessfully() throws IOException {
    // Arrange
    byte[] content = "test content".getBytes();
    MockMultipartFile multipartFile = new MockMultipartFile(
        "file",
        "test.txt",
        "text/plain",
        content);

    File file = new File();
    file.setId(1L);
    file.setFilename("test.txt");

    byte[] chunk1 = "test".getBytes();
    byte[] chunk2 = "content".getBytes();

    Chunk chunkEntity1 = new Chunk();
    chunkEntity1.setId(1L);
    chunkEntity1.setSizeOriginal(chunk1.length);

    Chunk chunkEntity2 = new Chunk();
    chunkEntity2.setId(2L);
    chunkEntity2.setSizeOriginal(chunk2.length);

    when(fileService.createFile(multipartFile)).thenReturn(file);
    when(chunkingService.chunkFile(any(InputStream.class)))
        .thenReturn(Stream.of(chunk1, chunk2));
    when(chunkService.findOrCreateChunk(chunk1)).thenReturn(chunkEntity1);
    when(chunkService.findOrCreateChunk(chunk2)).thenReturn(chunkEntity2);

    // Act
    File result = fileProcessingService.processAndSplitFile(multipartFile);

    // Assert
    assertThat(result).isEqualTo(file);
    verify(fileService).createFile(multipartFile);
    verify(chunkingService).chunkFile(any(InputStream.class));
    verify(chunkService).findOrCreateChunk(chunk1);
    verify(chunkService).findOrCreateChunk(chunk2);
    verify(fileChunkService).createFileChunk(file, chunkEntity1, 0, 0);
    verify(fileChunkService).createFileChunk(file, chunkEntity2, 1, chunk1.length);
  }

  @Test
  void processAndSplitFile_ShouldThrowException_WhenIOExceptionOccurs() throws IOException {
    // Arrange
    MockMultipartFile originalFile = new MockMultipartFile(
        "file",
        "test.txt",
        "text/plain",
        "test content".getBytes());

    MultipartFile multipartFile = spy(originalFile);

    File file = new File();
    when(fileService.createFile(multipartFile)).thenReturn(file);
    doThrow(new IOException("Test exception"))
        .when(multipartFile).getInputStream();

    // Act & Assert
    FileProcessingException exception = assertThrows(
        FileProcessingException.class,
        () -> fileProcessingService.processAndSplitFile(multipartFile));

    // Act & Assert
    assertThat(exception)
        .hasMessageContaining("Test exception");

  }
}
