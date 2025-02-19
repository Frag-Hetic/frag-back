package com.projet.hetic.frag.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.exception.EntityNotFoundException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.repository.FileRepository;

@Service
public class FileService {
  private final FileMapper fileMapper;
  private final HashingService hashingService;
  private final FileRepository fileRepository;

  public FileService(FileMapper fileMapper, HashingService hashingService, FileRepository fileRepository,
      ChunkingService chunkingService, ChunkService chunkService, FileChunkService fileChunkService) {
    this.fileMapper = fileMapper;
    this.hashingService = hashingService;
    this.fileRepository = fileRepository;
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
  public File getFileById(String fileId) {
    Long id;
    try {
      id = Long.parseLong(fileId);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Invalid file ID format", e);
    }
    return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
  }
}
