package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.model.File;

@ExtendWith(MockitoExtension.class)
class FileProcessingServiceTest {

  @Mock
  private FileChunkProcessor fileChunkProcessor;

  @Mock
  private FileReconstructionProcessor fileReconstructionProcessor;

  @InjectMocks
  private FileProcessingService fileProcessingService;

  private MockMultipartFile mockMultipartFile;
  private ChunkingParamsDto mockParams;
  private File mockFile;
  private FileDownloadDTO mockDownloadDTO;

  @BeforeEach
  void setUp() {
    // Configuration du fichier mock
    mockMultipartFile = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Contenu test".getBytes());

    // Configuration des paramètres de chunking
    mockParams = new ChunkingParamsDto();
    mockParams.setWindowSize(48);
    mockParams.setChunkMinSize(1024);
    mockParams.setChunkMaxSize(8192);
    mockParams.setBreakpointMask("0x1FFF");

    // Configuration du fichier résultant
    mockFile = new File();
    mockFile.setId(1L);
    mockFile.setFilename("test.txt");

    // Configuration du DTO de téléchargement
    mockDownloadDTO = new FileDownloadDTO();
    mockDownloadDTO.setFilename("test.txt");
    mockDownloadDTO.setMimeType(MediaType.TEXT_PLAIN_VALUE);
    mockDownloadDTO.setFileContent("Contenu test".getBytes());
  }

  @Test
  void processAndSplitFile_ShouldDelegateToProcessor() {
    // Arrange
    when(fileChunkProcessor.processFile(mockMultipartFile, mockParams))
        .thenReturn(mockFile);

    // Act
    File result = fileProcessingService.processAndSplitFile(mockMultipartFile, mockParams);

    // Assert
    assertThat(result).isNotNull()
        .isEqualTo(mockFile);
    verify(fileChunkProcessor).processFile(mockMultipartFile, mockParams);
  }

  @Test
  void processAndSplitFile_WithNullParams_ShouldDelegateToProcessor() {
    // Arrange
    when(fileChunkProcessor.processFile(mockMultipartFile, null))
        .thenReturn(mockFile);

    // Act
    File result = fileProcessingService.processAndSplitFile(mockMultipartFile, null);

    // Assert
    assertThat(result).isNotNull()
        .isEqualTo(mockFile);
    verify(fileChunkProcessor).processFile(mockMultipartFile, null);
  }

  @Test
  void processAndUnsplitFile_ShouldDelegateToProcessor() {
    // Arrange
    Long fileId = 1L;
    when(fileReconstructionProcessor.reconstructFile(fileId))
        .thenReturn(mockDownloadDTO);

    // Act
    FileDownloadDTO result = fileProcessingService.processAndUnsplitFile(fileId);

    // Assert
    assertThat(result).isNotNull()
        .isEqualTo(mockDownloadDTO);
    verify(fileReconstructionProcessor).reconstructFile(fileId);
  }
}