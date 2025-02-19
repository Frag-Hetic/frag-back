package com.projet.hetic.frag.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.service.FileProcessingService;
import com.projet.hetic.frag.service.FileService;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

  @Mock
  private FileService fileService;

  @Mock
  private FileProcessingService fileProcessingService;

  @InjectMocks
  private FileController fileController;

  private File testFile1;
  private File testFile2;
  private MockMultipartFile multipartMock;
  private FileDownloadDTO fileDownloadDTO;

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
  void splitFile_ShouldProcessAndReturnFile() throws Exception {
    // Arrange
    MockMultipartFile mockFile = multipartMock;
    File expectedFile = testFile1;

    when(fileProcessingService.processAndSplitFile(mockFile)).thenReturn(expectedFile);

    // Act
    ResponseEntity<File> response = fileController.splitFile(mockFile);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertNotNull(response.getBody());
    assertEquals(expectedFile, response.getBody());
  }

  @Test
  void getChunks_ShouldReturnSuccessMessage() {
    // Arrange
    MultipartFile file = mock(MultipartFile.class);

    // Act
    ResponseEntity<String> response = fileController.getChunks(file);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("hello");
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
}
