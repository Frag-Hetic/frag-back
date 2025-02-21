package com.projet.hetic.frag.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.model.File;

@ExtendWith(MockitoExtension.class)
class FileMapperTest {

  @InjectMocks
  private FileMapper fileMapper;

  @Mock
  private ChunkingConfig chunkingConfig;

  private MultipartFile mockMultipartFile;
  private File testFile;
  private byte[] testContent;

  @BeforeEach
  void setUp() {
    fileMapper = new FileMapper();
    testFile = new File();
    testContent = "test content".getBytes();
    // Configuration du mock MultipartFile
    mockMultipartFile = new MockMultipartFile(
        "testFile",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Contenu test".getBytes());

  }

  @Test
  void splitInputToEntity_ShouldMapAllFields() {
    // Configuration du mock ChunkingConfig
    when(chunkingConfig.getWindowSize()).thenReturn(48);
    when(chunkingConfig.getChunkMinSize()).thenReturn(1024);
    when(chunkingConfig.getChunkMaxSize()).thenReturn(8192);
    when(chunkingConfig.getBreakpointMask()).thenReturn("0x1FFF");

    // Act
    File result = fileMapper.splitInputToEntity(mockMultipartFile, chunkingConfig);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(file -> {
          assertThat(file.getFileSize()).isEqualTo(mockMultipartFile.getSize());
          assertThat(file.getCompressedFileSize()).isEqualTo(0L);
          assertThat(file.getFileName()).isEqualTo("test.txt");
          assertThat(file.getMimeType()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
          assertThat(file.getWindowSize()).isEqualTo(48);
          assertThat(file.getChunkMinSize()).isEqualTo(1024);
          assertThat(file.getChunkMaxSize()).isEqualTo(8192);
          assertThat(file.getBreakpointMask()).isEqualTo("0x1FFF");
        });
  }

  @Test
  void splitInputToEntity_WithEmptyFile_ShouldMapCorrectly() {
    // Arrange
    MockMultipartFile emptyFile = new MockMultipartFile(
        "emptyFile",
        "empty.txt",
        MediaType.TEXT_PLAIN_VALUE,
        new byte[0]);

    // Act
    File result = fileMapper.splitInputToEntity(emptyFile, chunkingConfig);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(file -> {
          assertThat(file.getFileSize()).isZero();
          assertThat(file.getFileName()).isEqualTo("empty.txt");
        });
  }

  @Test
  void splitInputToEntity_WithNullFilename_ShouldMapCorrectly() {
    // Arrange
    MockMultipartFile fileWithNullName = new MockMultipartFile(
        "file",
        "",
        MediaType.TEXT_PLAIN_VALUE,
        "Contenu".getBytes());

    // Act
    File result = fileMapper.splitInputToEntity(fileWithNullName, chunkingConfig);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(file -> {
          assertThat(file.getFileName()).isEmpty();
          assertThat(file.getMimeType()).isEqualTo(MediaType.TEXT_PLAIN_VALUE);
        });
  }

  @Test
  void toDownloadDTO_ShouldMapAllFields() {
    // Arrange
    testFile.setFileName("test.txt");
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
    testFile.setFileName(null);
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
    testFile.setFileName("empty.txt");
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