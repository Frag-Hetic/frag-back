package com.projet.hetic.frag.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.projet.hetic.frag.model.Chunk;

class ChunkMapperTest {

  private ChunkMapper chunkMapper;

  @BeforeEach
  void setUp() {
    chunkMapper = new ChunkMapper();
  }

  @Test
  void bytesToEntity_WithNonEmptyBytes_ShouldReturnChunkWithCorrectSize() {
    // Given
    byte[] testBytes = "test data".getBytes();

    // When
    Chunk result = chunkMapper.bytesToEntity(testBytes);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getSizeOriginal()).isEqualTo(testBytes.length);
  }

  @Test
  void bytesToEntity_WithEmptyBytes_ShouldReturnChunkWithZeroSize() {
    // Given
    byte[] emptyBytes = new byte[0];

    // When
    Chunk result = chunkMapper.bytesToEntity(emptyBytes);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getSizeOriginal()).isZero();
  }

  @Test
  void bytesToEntity_WithNull_ShouldThrowNullPointerException() {
    // Then
    assertThatThrownBy(() -> chunkMapper.bytesToEntity(null))
        .isInstanceOf(NullPointerException.class);
  }
}
