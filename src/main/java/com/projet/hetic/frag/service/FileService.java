package com.projet.hetic.frag.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.repository.FileRepository;

@Service
public class FileService {
  private final FileMapper fileMapper;
  private final HashingService hashingService;
  private final FileRepository fileRepository;
  private final ChunkingService chunkingService;
  private final ChunkService chunkService;

  public FileService(FileMapper fileMapper, HashingService hashingService, FileRepository fileRepository,
      ChunkingService chunkingService, ChunkService chunkService) {
    this.fileMapper = fileMapper;
    this.hashingService = hashingService;
    this.fileRepository = fileRepository;
    this.chunkingService = chunkingService;
    this.chunkService = chunkService;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File createFile(MultipartFile multipartFile) {
    File file = fileMapper.multipartToEntity(multipartFile);

    byte[] bytes;
    try {
      bytes = multipartFile.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read multipart file", e);
    }

    String hash = hashingService.hashAndCrypt64(bytes);
    file.setCheckhash(hash);

    return fileRepository.save(file);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public List<File> getAllFile() {
    return fileRepository.findAll();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void processAndSplitFile(MultipartFile multipartFile) {
    // Créer l'entité File
    // File file = createFile(multipartFile);

    try {
      InputStream inputStream = multipartFile.getInputStream();
      Stream<byte[]> chunks = chunkingService.chunkFile(inputStream);
      chunks.forEach(
          chunk -> chunkService.findOrCreateChunk(chunk));
      // return file;
    } catch (IOException e) {
      throw new FileProcessingException(e.getMessage());
    }
  }
}
