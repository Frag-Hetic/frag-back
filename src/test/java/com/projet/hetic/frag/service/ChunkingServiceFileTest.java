package com.projet.hetic.frag.service;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ChunkingServiceFileTest {

    private final ChunkingService chunkingService = new ChunkingService();

    @Test
    void testChunkingWithDifferentFileTypes() throws IOException {
        byte[] textFile = Files.readAllBytes(Paths.get("src/test/resources/test.txt"));
        byte[] imageFile = Files.readAllBytes(Paths.get("src/test/resources/test.png"));
        byte[] binaryFile = Files.readAllBytes(Paths.get("src/test/resources/test.zip"));

        assertFalse(chunkingService.chunkFile(new ByteArrayInputStream(textFile)).collect(Collectors.toList()).isEmpty());
        assertFalse(chunkingService.chunkFile(new ByteArrayInputStream(imageFile)).collect(Collectors.toList()).isEmpty());
        assertFalse(chunkingService.chunkFile(new ByteArrayInputStream(binaryFile)).collect(Collectors.toList()).isEmpty());
    }
}
