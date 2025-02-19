package com.projet.hetic.frag.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.model.File;

@Service
public class FileProcessingService {
  private final FileService fileService;
  private final ChunkingService chunkingService;
  private final ChunkService chunkService;
  private final FileChunkService fileChunkService;

  public FileProcessingService(FileService fileService,
      ChunkingService chunkingService,
      ChunkService chunkService, FileChunkService fileChunkService) {
    this.fileService = fileService;
    this.chunkingService = chunkingService;
    this.chunkService = chunkService;
    this.fileChunkService = fileChunkService;
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

}
