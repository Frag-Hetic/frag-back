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
import com.projet.hetic.frag.model.FileChunk;

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
    System.out.println("Segment " + file.getId() + ": size -> "
        + new String(file.getFileSize() + " bytes, compressed size -> "));

    try {
      AtomicInteger order = new AtomicInteger(0);
      AtomicInteger offsetStart = new AtomicInteger(0);
      InputStream inputStream = multipartFile.getInputStream();
      Stream<byte[]> chunks = chunkingService.chunkFile(inputStream);
      chunks.map(chunk -> chunkService.findOrCreateChunk(chunk)).forEach(chunk -> {
        fileChunkService.createFileChunk(file, chunk, order.get(),
            offsetStart.get());
        order.getAndIncrement();
        offsetStart.addAndGet(chunk.getSizeOriginal());
      });
      return file;
    } catch (IOException e) {
      throw new FileProcessingException(e.getMessage());
    }
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public FileDownloadDTO processAndUnsplitFile(Long fileId) {
    try {
      File file = fileService.getFileById(fileId);

      // Retrieve sorted file chunks
      List<FileChunk> fileChunks = fileChunkService.getFileChunkByFile(fileId);

      if (fileChunks.isEmpty()) {
        throw new FileProcessingException("No chunks found for file ID: " + fileId);
      }
      // Reconstruct file content
      try (ByteArrayOutputStream fileContent = new ByteArrayOutputStream()) {
        for (FileChunk fileChunk : fileChunks) {
          System.out.println("Processing chunk: " + fileChunk.getChunk().getId());
          Chunk chunk = fileChunk.getChunk();
          byte[] compressedData = chunk.getData();
          byte[] uncompressedData = compressionService.decompressChunk(compressedData);
          fileContent.write(uncompressedData);
        }

        // Return the reconstructed file
        return new FileDownloadDTO(file.getFilename(), file.getMimeType(), fileContent.toByteArray());

      } catch (IOException e) {
        throw new FileProcessingException("Error reconstructing file: " + e.getMessage());
      }

    } catch (RuntimeException e) {
      throw new FileProcessingException("Failed to process file: " + e.getMessage());
    }
  }
}
