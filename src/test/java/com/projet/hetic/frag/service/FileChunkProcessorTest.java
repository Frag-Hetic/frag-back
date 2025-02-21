package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FileChunkProcessorTest {

  @Mock
  private FileService fileService;

  @Mock
  private ChunkService chunkService;

  @Mock
  private FileChunkService fileChunkService;

  @Mock
  private ChunkingConfig defaultConfig;

  @Mock
  private TimeUtils timeUtils;

  @Mock
  private ChunkingService chunkingService;

  @InjectMocks
  private FileChunkProcessor fileChunkProcessor;

  private MockMultipartFile mockMultipartFile;
  private ChunkingParamsDto mockParams;
  private File mockFile;
  private Chunk mockChunk;

  @BeforeEach
  void setUp() {
    mockMultipartFile = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "Test content".getBytes());

    mockParams = new ChunkingParamsDto();
    mockParams.setWindowSize(48);
    mockParams.setChunkMinSize(1024);
    mockParams.setChunkMaxSize(8192);
    mockParams.setBreakpointMask("0x1FFF");

    mockFile = new File();
    mockFile.setId(1L);
    mockFile.setFileName("test.txt");

    mockChunk = new Chunk();
    mockChunk.setId(1L);
    mockChunk.setSizeOriginal(100);
    mockChunk.setSizeCompressed(50);
    mockChunk.setData("test-data".getBytes());
  }

  @Test
  void processFile_WithValidParams_ShouldProcessSuccessfully() throws IOException {
    // Arrange
    ChunkingConfig expectedConfig = new ChunkingConfig();
    expectedConfig.setWindowSize(48);
    expectedConfig.setChunkMinSize(1024);
    expectedConfig.setChunkMaxSize(8192);
    expectedConfig.setBreakpointMask("0x1FFF");

    when(fileService.createFile(eq(mockMultipartFile), any(ChunkingConfig.class)))
        .thenReturn(mockFile);
    when(chunkingService.chunkFile(any(InputStream.class)))
        .thenReturn(Stream.of(mockChunk.getData()));
    when(chunkService.findOrCreateChunk(any()))
        .thenReturn(mockChunk);
    when(fileService.updateFile(mockFile))
        .thenReturn(mockFile);
    when(timeUtils.getFormattedProcessingTime(anyLong()))
        .thenReturn("1s");

    // Act
    File result = fileChunkProcessor.processFile(mockMultipartFile, mockParams);

    // Assert
    assertThat(result).isNotNull().isEqualTo(mockFile);
    verify(fileService).createFile(eq(mockMultipartFile), any(ChunkingConfig.class));
    verify(fileService).updateFile(mockFile);
    verify(fileChunkService).createFileChunk(eq(mockFile), eq(mockChunk), anyInt(), anyLong());
  }

  @Test
  void processFile_WhenIOException_ShouldThrowFileProcessingException() throws IOException {
    // Arrange
    doThrow(new FileProcessingException("Test error"))
        .when(fileService)
        .createFile(any(MultipartFile.class), any(ChunkingConfig.class));

    // Act & Assert
    assertThatThrownBy(() -> fileChunkProcessor.processFile(mockMultipartFile, mockParams))
        .isInstanceOf(FileProcessingException.class)
        .hasMessageContaining("Test error");
  }

  @Test
  void createChunkingConfig_WithValidParams_ShouldReturnConfig() {
    // Act
    ChunkingConfig result = fileChunkProcessor.createChunkingConfig(mockParams);

    // Assert
    assertThat(result)
        .isNotNull()
        .satisfies(config -> {
          assertThat(config.getWindowSize()).isEqualTo(48);
          assertThat(config.getChunkMinSize()).isEqualTo(1024);
          assertThat(config.getChunkMaxSize()).isEqualTo(8192);
          assertThat(config.getBreakpointMask()).isEqualTo("0x1FFF");
        });
  }

  @Test
  void createChunkingConfig_WithNullParams_ShouldReturnDefaultConfig() {
    // Act
    ChunkingConfig result = fileChunkProcessor.createChunkingConfig(null);

    // Assert
    assertThat(result).isEqualTo(defaultConfig);
  }
}