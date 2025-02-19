package com.projet.hetic.frag.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.model.File;

class FileMapperTest {

  private FileMapper fileMapper;
  private MultipartFile mockMultipartFile;

  @BeforeEach
  void setUp() {
    fileMapper = new FileMapper();
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
}