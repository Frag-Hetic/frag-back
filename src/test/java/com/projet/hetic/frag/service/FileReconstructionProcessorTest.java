package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;

@ExtendWith(MockitoExtension.class)
class FileReconstructionProcessorTest {

  @Mock
  private FileService fileService;

  @Mock
  private FileChunkService fileChunkService;

  @Mock
  private FileConstructionService fileConstructionService;

  @Mock
  private HashingService hashingService;

  @Mock
  private FileMapper fileMapper;

  @InjectMocks
  private FileReconstructionProcessor fileReconstructionProcessor;

  private File mockFile;
  private List<FileChunk> mockFileChunks;
  private FileDownloadDTO mockDownloadDTO;
  private byte[] mockContent;

  @BeforeEach
  void setUp() {
    // Préparation du fichier mock
    mockFile = new File();
    mockFile.setId(1L);
    mockFile.setFileName("test.txt");
    mockFile.setCheckhash("mockhash123");

    // Préparation des chunks
    mockFileChunks = new ArrayList<>();
    FileChunk chunk = new FileChunk();
    chunk.setFile(mockFile);
    chunk.setChunkOrder(1);
    mockFileChunks.add(chunk);

    // Préparation du contenu
    mockContent = "Test content".getBytes();

    // Préparation du DTO
    mockDownloadDTO = new FileDownloadDTO();
    mockDownloadDTO.setFilename("test.txt");
    mockDownloadDTO.setFileContent(mockContent);
  }

  @Test
  void reconstructFile_WithValidFileAndChunks_ShouldReturnDTO() {
    // Arrange
    when(fileService.getFileById(1L)).thenReturn(mockFile);
    when(fileChunkService.getFileChunkByFile(1L)).thenReturn(mockFileChunks);
    when(fileConstructionService.reconstructFileFromChunks(mockFileChunks, 1L))
        .thenReturn(mockContent);
    when(hashingService.compareConstructFileWithCheckHash(mockContent, "mockhash123"))
        .thenReturn(true);
    when(fileMapper.toDownloadDTO(mockFile, mockContent))
        .thenReturn(mockDownloadDTO);

    // Act
    FileDownloadDTO result = fileReconstructionProcessor.reconstructFile(1L);

    // Assert
    assertThat(result)
        .isNotNull()
        .isEqualTo(mockDownloadDTO);
  }

  @Test
  void reconstructFile_WithEmptyChunks_ShouldReturnEmptyDTO() {
    // Arrange
    when(fileService.getFileById(1L)).thenReturn(mockFile);
    when(fileChunkService.getFileChunkByFile(1L)).thenReturn(new ArrayList<>());
    when(fileMapper.toDownloadDTO(mockFile, new byte[] {}))
        .thenReturn(mockDownloadDTO);

    // Act
    FileDownloadDTO result = fileReconstructionProcessor.reconstructFile(1L);

    // Assert
    assertThat(result).isEqualTo(mockDownloadDTO);
  }

  @Test
  void reconstructFile_WhenHashVerificationFails_ShouldThrowException() {
    // Arrange
    when(fileService.getFileById(1L)).thenReturn(mockFile);
    when(fileChunkService.getFileChunkByFile(1L)).thenReturn(mockFileChunks);
    when(fileConstructionService.reconstructFileFromChunks(mockFileChunks, 1L))
        .thenReturn(mockContent);
    when(hashingService.compareConstructFileWithCheckHash(mockContent, "mockhash123"))
        .thenReturn(false);

    // Act & Assert
    assertThatThrownBy(() -> fileReconstructionProcessor.reconstructFile(1L))
        .isInstanceOf(FileProcessingException.class)
        .hasMessage("Error processing file: Hash verification failed");
  }

  @Test
  void reconstructFile_WhenFileNotFound_ShouldThrowException() {
    // Arrange
    when(fileService.getFileById(1L))
        .thenThrow(new FileProcessingException("File not found"));

    // Act & Assert
    assertThatThrownBy(() -> fileReconstructionProcessor.reconstructFile(1L))
        .isInstanceOf(FileProcessingException.class)
        .hasMessage("Error processing file: File not found");
  }
}