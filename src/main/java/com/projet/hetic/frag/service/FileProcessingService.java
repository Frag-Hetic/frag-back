package com.projet.hetic.frag.service;

import java.io.IOException;
import java.io.InputStream;
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

  public FileProcessingService(FileService fileService,
      ChunkingService chunkingService,
      ChunkService chunkService) {
    this.fileService = fileService;
    this.chunkingService = chunkingService;
    this.chunkService = chunkService;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File processAndSplitFile(MultipartFile multipartFile) {
    File file = fileService.createFile(multipartFile);
    try {
      InputStream inputStream = multipartFile.getInputStream();
      Stream<byte[]> chunks = chunkingService.chunkFile(inputStream);
      chunks.forEach(
          chunk -> chunkService.findOrCreateChunk(chunk));
      return file;
    } catch (IOException e) {
      throw new FileProcessingException(e.getMessage());
    }
  }

}
