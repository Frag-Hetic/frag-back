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
import com.projet.hetic.frag.mapper.FileMapper;
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
  private final HashingService hashingService;
  private final FileMapper fileMapper;

  public FileProcessingService(FileService fileService,
      ChunkingService chunkingService,
      ChunkService chunkService, FileChunkService fileChunkService, CompressionService compressionService,
      HashingService hashingService, FileMapper fileMapper) {
    this.fileService = fileService;
    this.chunkingService = chunkingService;
    this.chunkService = chunkService;
    this.fileChunkService = fileChunkService;
    this.compressionService = compressionService;
    this.hashingService = hashingService;
    this.fileMapper = fileMapper;
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
          Chunk chunk = fileChunk.getChunk();
          byte[] compressedData = chunk.getData();
          byte[] uncompressedData = compressionService.decompressChunk(compressedData);
          fileContent.write(uncompressedData);
        }

        // Compare file hash and new fileContent hash
        byte[] fileContentBytes = fileContent.toByteArray();
        String fileContentHash = hashingService.hashAndCrypt64(fileContentBytes);
        if (!fileContentHash.equals(file.getCheckhash())) {
          throw new FileProcessingException("File content hash mismatch");
        }

        // Return the reconstructed file
        return fileMapper.toDownloadDTO(file, fileContentBytes);

      } catch (IOException e) {
        throw new FileProcessingException("Error reconstructing file: " + e.getMessage());
      }

    } catch (RuntimeException e) {
      throw new FileProcessingException("Failed to process file: " + e.getMessage());
    }
  }
}
