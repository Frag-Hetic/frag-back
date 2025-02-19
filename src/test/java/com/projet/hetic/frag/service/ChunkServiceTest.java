package com.projet.hetic.frag.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.projet.hetic.frag.mapper.ChunkMapper;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.repository.ChunkRepository;

class ChunkServiceTest {

    @Mock
    private ChunkRepository chunkRepository;

    @Mock
    private CompressionService compressionService;

    @Mock
    private HashingService hashingService;

    @Mock
    private ChunkMapper chunkMapper;

    @InjectMocks
    private ChunkService chunkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindOrCreateChunk_WhenChunkExists() {
        byte[] data = "test".getBytes();
        byte[] compressedData = "compressed".getBytes();
        String hash = "hash123";

        Chunk existingChunk = new Chunk();
        existingChunk.setHash(hash);

        when(compressionService.compressChunk(data)).thenReturn(compressedData);
        when(hashingService.hashAndCrypt64(compressedData)).thenReturn(hash);
        when(chunkRepository.findByHash(hash)).thenReturn(Optional.of(existingChunk));

        Chunk result = chunkService.findOrCreateChunk(data);

        assertEquals(hash, result.getHash());
        verify(chunkRepository, never()).save(any(Chunk.class)); // Vérifie que la sauvegarde n’a pas été appelée
    }

    @Test
    void testFindOrCreateChunk_WhenChunkDoesNotExist() {
        byte[] data = "test".getBytes();
        byte[] compressedData = "compressed".getBytes();
        String hash = "hash123";

        when(compressionService.compressChunk(data)).thenReturn(compressedData);
        when(hashingService.hashAndCrypt64(compressedData)).thenReturn(hash);
        when(chunkRepository.findByHash(hash)).thenReturn(Optional.empty());

        Chunk newChunk = new Chunk();
        newChunk.setHash(hash);

        when(chunkMapper.bytesToEntity(data)).thenReturn(newChunk);
        when(chunkRepository.save(any(Chunk.class))).thenReturn(newChunk);

        Chunk result = chunkService.findOrCreateChunk(data);

        assertEquals(hash, result.getHash());
        verify(chunkRepository).save(any(Chunk.class)); // Vérifie que la sauvegarde a bien été effectuée
    }
}
