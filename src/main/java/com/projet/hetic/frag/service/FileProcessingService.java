package com.projet.hetic.frag.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.File;

@Service
public class FileProcessingService {
  private final FileService fileService;
  private final ChunkingService chunkingService;
  private final ChunkService chunkService;
  private final FileChunkService fileChunkService;
  private final CompressionService compressionService;

  public FileProcessingService(FileService fileService,
      ChunkingService chunkingService,
      ChunkService chunkService, FileChunkService fileChunkService, CompressionService compressionService) {
    this.fileService = fileService;
    this.chunkingService = chunkingService;
    this.chunkService = chunkService;
    this.fileChunkService = fileChunkService;
    this.compressionService = compressionService;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File processAndSplitFile(MultipartFile multipartFile) {
    File file = fileService.createFile(multipartFile);

    try {
      AtomicInteger order = new AtomicInteger(0);
      AtomicInteger offsetStart = new AtomicInteger(0);
      InputStream inputStream = multipartFile.getInputStream();
      Stream<byte[]> chunks = chunkingService.chunkFile(inputStream);
      chunks.map(chunk -> chunkService.findOrCreateChunk(chunk)).forEach(chunk -> {
        fileChunkService.createFileChunk(file, chunk, order.get(), offsetStart.get());
        order.getAndIncrement();
        offsetStart.addAndGet(chunk.getSizeOriginal());
      });
      return file;
    } catch (IOException e) {
      throw new FileProcessingException(e.getMessage());
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public FileDownloadDTO processAndUnsplitFile(String fileId) {
    try {
      File file = fileService.getFileById(fileId);

      // The chunks are already sorted by order in the repo
      List<Chunk> fileChunks = chunkService.getChunksByFile(fileId);

      try (ByteArrayOutputStream fileContent = new ByteArrayOutputStream()) {
        for (Chunk chunk : fileChunks) {
          byte[] uncompressedData = compressionService.decompressChunk(chunk.getData());
          fileContent.write(uncompressedData); // Append decompressed chunk
        }
        return new FileDownloadDTO(file.getFilename(), fileContent.toByteArray(), file.getMimeType());
      } catch (IOException e) {
        throw new FileProcessingException("Error processing file: " + e.getMessage());
      }

    } catch (RuntimeException e) {
      throw new FileProcessingException("Failed to process file: " + e.getMessage());
    }
  }
}
