package com.projet.hetic.frag.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;

class CompressionServiceTest {

    private CompressionService compressionService;

    @BeforeEach
    void setUp() {
        compressionService = new CompressionService();
    }

    @Test
    void compressChunk_ShouldCompressData() {
        // Arrange
        String testString = "Test string with repeating content. ".repeat(100);
        byte[] originalData = testString.getBytes();

        // Act
        byte[] compressedData = compressionService.compressChunk(originalData);

        // Assert
        assertThat(compressedData.length).isLessThan(originalData.length);
    }

    @Test
    void decompressChunk_ShouldRestoreOriginalData() {
        // Arrange
        String testString = "Test string that should be compressed and decompressed";
        byte[] originalData = testString.getBytes();

        // Act
        byte[] compressedData = compressionService.compressChunk(originalData);
        byte[] decompressedData = compressionService.decompressChunk(compressedData);

        // Assert
        assertThat(decompressedData).isEqualTo(originalData);
    }

    @Test
    void compressChunk_ShouldHandleEmptyArray() {
        // Arrange
        byte[] emptyData = new byte[0];

        // Act
        byte[] compressedData = compressionService.compressChunk(emptyData);
        byte[] decompressedData = compressionService.decompressChunk(compressedData);

        // Assert
        assertThat(decompressedData).isEmpty();
    }

    @Test
    void compressChunk_ShouldHandleLargeData() {
        // Arrange
        byte[] largeData = new byte[1024 * 1024]; // 1MB de données
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i % 256);
        }

        // Act
        byte[] compressedData = compressionService.compressChunk(largeData);
        byte[] decompressedData = compressionService.decompressChunk(compressedData);

        // Assert
        assertThat(decompressedData).isEqualTo(largeData);
    }

    @Test
    void decompressChunk_ShouldThrowException_WhenDataIsInvalid() {
        // Arrange
        byte[] invalidData = "Invalid compressed data".getBytes();

        // Act & Assert
        assertThatThrownBy(() -> compressionService.decompressChunk(invalidData))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erreur lors de la décompression");
    }
}
