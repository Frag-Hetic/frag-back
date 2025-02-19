package com.projet.hetic.frag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HashingServiceTest {

  private HashingService hashingService;

  @BeforeEach
  void setUp() {
    hashingService = new HashingService();
  }

  @Test
  void hashAndCrypt64_ShouldReturnCorrectHash_WhenGivenEmptyByteArray() {
    // Arrange
    byte[] emptyArray = new byte[0];
    // SHA-256 hash for empty byte array
    String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    // Act
    String result = hashingService.hashAndCrypt64(emptyArray);

    // Assert
    assertThat(result).isEqualTo(expectedHash);
  }

  @Test
  void hashAndCrypt64_ShouldReturnCorrectHash_WhenGivenTestString() {
    // Arrange
    byte[] testData = "test".getBytes();
    // SHA-256 hash for "test"
    String expectedHash = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";

    // Act
    String result = hashingService.hashAndCrypt64(testData);

    // Assert
    assertThat(result).isEqualTo(expectedHash);
  }

  @Test
  void hashAndCrypt64_ShouldReturnSameHash_ForSameInput() {
    // Arrange
    byte[] testData = "Hello, World!".getBytes();

    // Act
    String firstHash = hashingService.hashAndCrypt64(testData);
    String secondHash = hashingService.hashAndCrypt64(testData);

    // Assert
    assertThat(firstHash).isEqualTo(secondHash);
  }

  @Test
  void hashAndCrypt64_ShouldReturnDifferentHash_ForDifferentInput() {
    // Arrange
    byte[] firstData = "Hello".getBytes();
    byte[] secondData = "World".getBytes();

    // Act
    String firstHash = hashingService.hashAndCrypt64(firstData);
    String secondHash = hashingService.hashAndCrypt64(secondData);

    // Assert
    assertThat(firstHash).isNotEqualTo(secondHash);
  }

  @Test
  void hashAndCrypt64_ShouldReturnHashWithCorrectLength() {
    // Arrange
    byte[] testData = "test".getBytes();
    int expectedLength = 64; // SHA-256 produces 256 bits = 64 hex characters

    // Act
    String hash = hashingService.hashAndCrypt64(testData);

    // Assert
    assertThat(hash).hasSize(expectedLength);
  }

  @Test
  void hashAndCrypt64_ShouldNotThrowException() {
    // Arrange
    byte[] testData = "test".getBytes();

    // Act & Assert
    assertThatCode(() -> hashingService.hashAndCrypt64(testData))
        .doesNotThrowAnyException();
  }
}