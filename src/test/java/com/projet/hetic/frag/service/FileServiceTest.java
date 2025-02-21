package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.repository.FileRepository;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

  @Mock
  private FileMapper fileMapper;

  @Mock
  private HashingService hashingService;

  @Mock
  private FileRepository fileRepository;

  @Mock
  private MultipartFile multipartFile;

  private FileService fileService;
  private ChunkingConfig defaultConfig;

  @BeforeEach
  void setUp() {
    defaultConfig = new ChunkingConfig();
    fileService = new FileService(fileMapper, hashingService, fileRepository);
  }

  @Test
  void createFile_ShouldCreateAndReturnFile() throws IOException {
    // Arrange
    byte[] fileContent = "test content".getBytes();
    File mappedFile = new File();
    String hash = "hashedContent";
    File savedFile = new File();

    when(multipartFile.getBytes()).thenReturn(fileContent);
    when(fileMapper.splitInputToEntity(multipartFile, defaultConfig)).thenReturn(mappedFile);
    when(hashingService.hashAndCrypt64(fileContent)).thenReturn(hash);
    when(fileRepository.save(mappedFile)).thenReturn(savedFile);

    // Act
    File result = fileService.createFile(multipartFile, defaultConfig);

    // Assert
    assertThat(result).isEqualTo(savedFile);
    verify(fileMapper).splitInputToEntity(multipartFile, defaultConfig);
    verify(hashingService).hashAndCrypt64(fileContent);
    verify(fileRepository).save(mappedFile);
  }

  @Test
  void createFile_WithCustomConfig_ShouldCreateAndReturnFile() throws IOException {
    // Arrange
    byte[] fileContent = "test content".getBytes();
    File mappedFile = new File();
    String hash = "hashedContent";
    File savedFile = new File();
    ChunkingConfig customConfig = new ChunkingConfig();
    customConfig.setWindowSize(32);

    when(multipartFile.getBytes()).thenReturn(fileContent);
    when(fileMapper.splitInputToEntity(multipartFile, customConfig)).thenReturn(mappedFile);
    when(hashingService.hashAndCrypt64(fileContent)).thenReturn(hash);
    when(fileRepository.save(mappedFile)).thenReturn(savedFile);

    // Act
    File result = fileService.createFile(multipartFile, customConfig);

    // Assert
    assertThat(result).isEqualTo(savedFile);
    verify(fileMapper).splitInputToEntity(multipartFile, customConfig);
  }

  @Test
  void getAllFile_ShouldReturnAllFiles() {
    // Arrange
    File file1 = new File();
    File file2 = new File();
    when(fileRepository.findAll()).thenReturn(Arrays.asList(file1, file2));

    // Act
    List<File> result = fileService.getAllFile();

    // Assert
    assertThat(result).hasSize(2).containsExactly(file1, file2);
  }

  @Test
  void getAllFile_WhenNoFiles_ShouldReturnEmptyList() {
    // Arrange
    when(fileRepository.findAll()).thenReturn(Collections.emptyList());

    // Act
    List<File> result = fileService.getAllFile();

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  void getFileById_WhenFileExists_ShouldReturnFile() {
    // Arrange
    Long id = 1L;
    File file = new File();
    when(fileRepository.findById(id)).thenReturn(Optional.of(file));

    // Act
    File result = fileService.getFileById(id);

    // Assert
    assertThat(result).isEqualTo(file);
  }

  @Test
  void createFile_WhenIOException_ShouldThrowRuntimeException() throws IOException {
    // Arrange
    when(multipartFile.getBytes()).thenThrow(new IOException("Test exception"));

    // Act & Assert
    assertThatThrownBy(() -> fileService.createFile(multipartFile, defaultConfig))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to read multipart file");
  }

  @Test
  void updateFile_ShouldUpdateAndReturnFile() {
    // Arrange
    File file = new File();
    File savedFile = new File();
    when(fileRepository.save(file)).thenReturn(savedFile);

    // Act
    File result = fileService.updateFile(file);

    // Assert
    assertThat(result).isEqualTo(savedFile);
    verify(fileRepository).save(file);
  }
}
