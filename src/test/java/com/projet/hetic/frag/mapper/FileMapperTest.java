package com.projet.hetic.frag.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.model.File;

class FileMapperTest {

  private FileMapper fileMapper;
  private MultipartFile mockMultipartFile;
  private File testFile;
  private byte[] testContent;

  @BeforeEach
  void setUp() {
    fileMapper = new FileMapper();
    testFile = new File();
    testContent = "test content".getBytes();
    mockMultipartFile = mock(MultipartFile.class);
  }

  @Test
  void multipartToEntity_ShouldMapAllFields() {
    // Arrange
    String filename = "test.txt";
    String contentType = "text/plain";
    long fileSize = 1024L;

    when(mockMultipartFile.getOriginalFilename()).thenReturn(filename);
    when(mockMultipartFile.getContentType()).thenReturn(contentType);
    when(mockMultipartFile.getSize()).thenReturn(fileSize);

    // Act
    File result = fileMapper.multipartToEntity(mockMultipartFile);

    // Assert
    assertEquals(filename, result.getFilename());
    assertEquals(contentType, result.getMimeType());
    assertEquals(fileSize, result.getFileSize());
  }

  @Test
  void toDownloadDTO_ShouldMapAllFields() {
    // Arrange
    testFile.setFilename("test.txt");
    testFile.setMimeType("text/plain");

    // Act
    FileDownloadDTO result = fileMapper.toDownloadDTO(testFile, testContent);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(dto -> {
          assertThat(dto.getFilename()).isEqualTo("test.txt");
          assertThat(dto.getMimeType()).isEqualTo("text/plain");
          assertThat(dto.getFileContent()).isEqualTo(testContent);
        });
  }

  @Test
  void toDownloadDTO_WithNullValues_ShouldMapNullValues() {
    // Arrange
    testFile.setFilename(null);
    testFile.setMimeType(null);

    // Act
    FileDownloadDTO result = fileMapper.toDownloadDTO(testFile, null);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(dto -> {
          assertThat(dto.getFilename()).isNull();
          assertThat(dto.getMimeType()).isNull();
          assertThat(dto.getFileContent()).isNull();
        });
  }

  @Test
  void toDownloadDTO_WithEmptyContent_ShouldMapEmptyContent() {
    // Arrange
    testFile.setFilename("empty.txt");
    testFile.setMimeType("text/plain");
    byte[] emptyContent = new byte[0];

    // Act
    FileDownloadDTO result = fileMapper.toDownloadDTO(testFile, emptyContent);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(dto -> {
          assertThat(dto.getFilename()).isEqualTo("empty.txt");
          assertThat(dto.getMimeType()).isEqualTo("text/plain");
          assertThat(dto.getFileContent()).isEmpty();
        });
  }
}