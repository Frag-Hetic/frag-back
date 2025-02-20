package com.projet.hetic.frag.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testTotalCompressedSize() {
        byte[] originalData1 = "Hello Frag! Hello Frag! Hello Frag! Hello Frag!".getBytes();
        byte[] originalData2 = "Data compression is important!".getBytes();

        byte[] compressed1 = compressionService.compressChunk(originalData1);
        byte[] compressed2 = compressionService.compressChunk(originalData2);

        int totalCompressedSize = compressionService.getTotalCompressedSize(List.of(compressed1, compressed2));

        assertTrue(totalCompressedSize < (originalData1.length + originalData2.length),
                "La taille compressée totale devrait être inférieure à la somme des tailles originales");
    }
}
