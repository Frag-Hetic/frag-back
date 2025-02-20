package com.projet.hetic.frag.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.exception.FileConstructionException;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.FileChunk;

@Service
public class FileConstructionService {
  private final CompressionService compressionService;

  public FileConstructionService(CompressionService compressionService) {
    this.compressionService = compressionService;
  }

  public byte[] reconstructFileFromChunks(List<FileChunk> fileChunks, Long fileId) {
    try (ByteArrayOutputStream fileContent = new ByteArrayOutputStream()) {
      for (FileChunk fileChunk : fileChunks) {
        Chunk chunk = fileChunk.getChunk();
        byte[] compressedData = chunk.getData();
        byte[] uncompressedData = compressionService.decompressChunk(compressedData);
        fileContent.write(uncompressedData);
      }
      return fileContent.toByteArray();
    } catch (IOException e) {
      throw new FileConstructionException(fileId, e.getMessage());
    }
  }

}
