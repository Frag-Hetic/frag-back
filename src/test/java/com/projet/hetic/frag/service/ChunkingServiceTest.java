package com.projet.hetic.frag.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChunkingServiceTest {

    private ChunkingService chunkingService;

    @BeforeEach
    void setUp() {
        chunkingService = new ChunkingService();
    }

    @Test
    void testChunkFile() {
        byte[] data = new byte[16 * 1024]; // 16KB de données
        InputStream inputStream = new ByteArrayInputStream(data);

        List<byte[]> chunks = chunkingService.chunkFile(inputStream).collect(Collectors.toList());

        assertFalse(chunks.isEmpty());
        assertTrue(chunks.get(0).length <= 8 * 1024); // Vérifie que chaque chunk est ≤ 8KB
    }

    @Test
    void testChunkFileWithEmptyInput() {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        List<byte[]> chunks = chunkingService.chunkFile(inputStream).collect(Collectors.toList());

        assertTrue(chunks.isEmpty());
    }

    @Test
    void testChunkingPerformance() {
        byte[] data = new byte[10 * 1024 * 1024]; // 10MB de données
        InputStream inputStream = new ByteArrayInputStream(data);

        long startTime = System.nanoTime();
        List<byte[]> chunks = chunkingService.chunkFile(inputStream).collect(Collectors.toList());
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000; // Convertir en ms
        System.out.println("Temps de découpage pour 10MB : " + durationMs + " ms");

        assertFalse(chunks.isEmpty());
    }
}
