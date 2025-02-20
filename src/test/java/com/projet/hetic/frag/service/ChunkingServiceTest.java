package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.projet.hetic.frag.config.ChunkingConfig;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChunkingServiceTest {

  @Mock
  private ChunkingConfig config;

  @InjectMocks
  private ChunkingService chunkingService;

  @BeforeEach
  void setUp() {
    // Configuration de base nécessaire pour tous les tests
    when(config.getWindowSize()).thenReturn(16);
    when(config.getBreakpointMask()).thenReturn("0x0FFF"); // Valeur hexadécimale valide
    when(config.getChunkMinSize()).thenReturn(64);
    when(config.getChunkMaxSize()).thenReturn(1024);
  }

  @Test
  void chunkFile_WhenInputIsEmpty_ShouldReturnEmptyStream() throws IOException {
    // Given
    InputStream emptyStream = new ByteArrayInputStream(new byte[0]);

    // When
    Stream<byte[]> result = chunkingService.chunkFile(emptyStream);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void chunkFile_WhenInputIsSmallerThanMinSize_ShouldCreateSingleChunk() throws IOException {
    // Given
    byte[] smallInput = "Small test input".getBytes();
    InputStream inputStream = new ByteArrayInputStream(smallInput);

    // When
    List<byte[]> chunks = chunkingService.chunkFile(inputStream).collect(Collectors.toList());

    // Then
    assertThat(chunks).hasSize(1);
    assertThat(chunks.get(0)).isEqualTo(smallInput);
  }

  @Test
  void chunkFile_WhenInputIsLargerThanMaxSize_ShouldSplitIntoMultipleChunks() throws IOException {
    // Given
    byte[] largeInput = new byte[2048]; // Larger than maxSize
    Arrays.fill(largeInput, (byte) 'A');
    InputStream inputStream = new ByteArrayInputStream(largeInput);

    // When
    List<byte[]> chunks = chunkingService.chunkFile(inputStream).collect(Collectors.toList());

    // Then
    assertThat(chunks).hasSizeGreaterThan(1);
    assertThat(chunks).allMatch(chunk -> chunk.length <= config.getChunkMaxSize());
  }

  @Test
  void chunkFile_WhenIOExceptionOccurs_ShouldPropagateException() {
    // Given
    InputStream failingStream = new InputStream() {
      @Override
      public int read() throws IOException {
        throw new IOException("Test exception");
      }
    };

    // When/Then
    assertThatThrownBy(() -> chunkingService.chunkFile(failingStream))
        .isInstanceOf(IOException.class)
        .hasMessage("Test exception");
  }

  @Test
  void chunkFile_WhenProcessingValidInput_ShouldRespectMinChunkSize() throws IOException {
    // Given
    byte[] input = new byte[100];
    Arrays.fill(input, (byte) 'X');
    InputStream inputStream = new ByteArrayInputStream(input);

    // When
    List<byte[]> chunks = chunkingService.chunkFile(inputStream).collect(Collectors.toList());

    // Then
    // Vérifie que tous les chunks sauf le dernier respectent la taille minimale
    for (int i = 0; i < chunks.size() - 1; i++) {
      assertThat(chunks.get(i).length).isGreaterThanOrEqualTo(config.getChunkMinSize());
    }

    // Vérifie que le dernier chunk est soit de taille minimale, soit contient le
    // reste des données
    if (chunks.size() > 0) {
      byte[] lastChunk = chunks.get(chunks.size() - 1);
      assertThat(lastChunk.length)
          .isLessThanOrEqualTo(config.getChunkMaxSize())
          .isGreaterThan(0);
    }
  }
}