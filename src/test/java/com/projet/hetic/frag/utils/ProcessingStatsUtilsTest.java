package com.projet.hetic.frag.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projet.hetic.frag.model.Chunk;

class ProcessingStatsUtilsTest {

  private ProcessingStatsUtils stats;
  private Chunk mockChunk;

  @BeforeEach
  void setUp() {
    stats = new ProcessingStatsUtils();
    mockChunk = new Chunk();
    mockChunk.setSizeOriginal(100);
    mockChunk.setSizeCompressed(50);
  }

  @Test
  void initialState_ShouldBeZero() {
    assertThat(stats.getOrder().get()).isZero();
    assertThat(stats.getOffset().get()).isZero();
    assertThat(stats.getTotalCompressedSize().get()).isZero();
  }

  @Test
  void updateStats_ShouldIncrementOrder() {
    // Act
    stats.updateStats(mockChunk);

    // Assert
    assertThat(stats.getOrder().get()).isEqualTo(1);
  }

  @Test
  void updateStats_ShouldAddToOffset() {
    // Act
    stats.updateStats(mockChunk);

    // Assert
    assertThat(stats.getOffset().get()).isEqualTo(100);
  }

  @Test
  void updateStats_ShouldAddToCompressedSize() {
    // Act
    stats.updateStats(mockChunk);

    // Assert
    assertThat(stats.getTotalCompressedSize().get()).isEqualTo(50);
  }

  @Test
  void updateStats_WithMultipleChunks_ShouldAccumulate() {
    // Arrange
    Chunk secondChunk = new Chunk();
    secondChunk.setSizeOriginal(200);
    secondChunk.setSizeCompressed(100);

    // Act
    stats.updateStats(mockChunk);
    stats.updateStats(secondChunk);

    // Assert
    assertThat(stats.getOrder().get()).isEqualTo(2);
    assertThat(stats.getOffset().get()).isEqualTo(300);
    assertThat(stats.getTotalCompressedSize().get()).isEqualTo(150);
  }

  @Test
  void updateStats_WithZeroSizeChunk_ShouldOnlyIncrementOrder() {
    // Arrange
    Chunk zeroChunk = new Chunk();
    zeroChunk.setSizeOriginal(0);
    zeroChunk.setSizeCompressed(0);

    // Act
    stats.updateStats(zeroChunk);

    // Assert
    assertThat(stats.getOrder().get()).isEqualTo(1);
    assertThat(stats.getOffset().get()).isZero();
    assertThat(stats.getTotalCompressedSize().get()).isZero();
  }
}