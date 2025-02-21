package com.projet.hetic.frag.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.projet.hetic.frag.exception.EntityNotFoundException;
import com.projet.hetic.frag.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.service.FileProcessingService;
import com.projet.hetic.frag.service.FileService;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

  @Mock
  private FileService fileService;

  @Mock
  private FileProcessingService fileProcessingService;

  @Mock
  private FileRepository fileRepository;

  @InjectMocks
  private FileController fileController;

  private File testFile1;
  private File testFile2;
  private MockMultipartFile multipartMock;
  private FileDownloadDTO fileDownloadDTO;
  private ChunkingParamsDto chunkingParams;

  @BeforeEach
  void setUp() {
    testFile1 = new File();
    testFile1.setId(1L);
    testFile1.setFilename("test1.txt");

    testFile2 = new File();
    testFile2.setId(2L);
    testFile2.setFilename("test2.txt");

    multipartMock = new MockMultipartFile(
        "file",
        "test1.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes());

    // Ajout des paramètres de chunking
    chunkingParams = new ChunkingParamsDto();
    chunkingParams.setWindowSize(16);
    chunkingParams.setChunkMinSize(64);
    chunkingParams.setChunkMaxSize(8192);
    chunkingParams.setBreakpointMask("0x3FF");

    fileDownloadDTO = new FileDownloadDTO(
        "test1.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Hello, World!".getBytes());
  }

  @Test
  void getFiles_ShouldReturnListOfFiles() {
    // Arrange
    List<File> expectedFiles = Arrays.asList(testFile1, testFile2);
    when(fileService.getAllFile()).thenReturn(expectedFiles);

    // Act
    ResponseEntity<List<File>> response = fileController.getFiles();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedFiles);
    verify(fileService).getAllFile();
  }

  @Test
  void getFileById_ShouldReturnFile() {
    // Arrange
    when(fileService.getFileById(1L)).thenReturn(testFile1);

    // Act
    ResponseEntity<File> response = fileController.getFileById(1L);
    File responseBody = response.getBody();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertEquals(testFile1, responseBody);
  }

  @Test
  void splitFile_WithValidParamsAndFile_ShouldReturnCreatedFile() {
    // Arrange
    when(fileProcessingService.processAndSplitFile(multipartMock, chunkingParams))
        .thenReturn(testFile1);

    // Act
    ResponseEntity<File> response = fileController.splitFile(multipartMock, chunkingParams);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(testFile1);
    assertThat(response.getHeaders().getLocation())
        .isEqualTo(URI.create("/files/1"));

    verify(fileProcessingService).processAndSplitFile(multipartMock, chunkingParams);
  }

  @Test
  void splitFile_WithDefaultParams_ShouldReturnCreatedFile() {
    // Arrange
    when(fileProcessingService.processAndSplitFile(multipartMock, null))
        .thenReturn(testFile1);

    // Act
    ResponseEntity<File> response = fileController.splitFile(multipartMock, null);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(testFile1);
    assertThat(response.getHeaders().getLocation())
        .isEqualTo(URI.create("/files/1"));

    verify(fileProcessingService).processAndSplitFile(multipartMock, null);
  }

  @Test
  void splitFile_WhenProcessingFails_ShouldThrowException() {
    // Arrange
    when(fileProcessingService.processAndSplitFile(multipartMock, chunkingParams))
        .thenThrow(new FileProcessingException("Erreur de traitement"));

    // Act & Assert
    assertThrows(FileProcessingException.class, () -> {
      fileController.splitFile(multipartMock, chunkingParams);
    });
  }

  @Test
  void unsplitFile_ShouldReturnFileContent() {
    // Arrange
    when(fileProcessingService.processAndUnsplitFile(1L))
        .thenReturn(fileDownloadDTO);

    // Act
    ResponseEntity<byte[]> response = fileController.unsplitFile(1L);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.TEXT_PLAIN);
    assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
        .contains("filename=\"test1.txt\"");
    assertThat(response.getBody())
        .isEqualTo("Hello, World!".getBytes());

    verify(fileProcessingService).processAndUnsplitFile(1L);
  }

  @Test
  void deleteFile_ShouldReturn200OK_WhenFileExists() {
    // Arrange
    Long fileId = 1L;
    doNothing().when(fileService).deleteFileById(fileId);

    // Act
    ResponseEntity<Map<String, String>> response = fileController.deleteFile(fileId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsEntry("status", "success");
    assertThat(response.getBody()).containsEntry("message", "File deleted successfully");
    verify(fileService).deleteFileById(fileId);
  }

  @Test
  void deleteFile_ShouldReturn404NotFound_WhenFileDoesNotExist() {
    // Arrange
    Long nonExistentFileId = 99L;
    doThrow(new EntityNotFoundException("File", "id", nonExistentFileId.toString()))
            .when(fileService).deleteFileById(nonExistentFileId);

    // Act & Assert
    EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> fileController.deleteFile(nonExistentFileId)
    );

    assertThat(exception.getMessage()).contains("File", "id", "99");
    verify(fileService).deleteFileById(nonExistentFileId);
  }

}
