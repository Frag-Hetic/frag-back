package com.projet.hetic.frag.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.dto.FileFilterDto;
import com.projet.hetic.frag.exception.EntityNotFoundException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.repository.FileRepository;
import com.projet.hetic.frag.specification.FileSpecification;

@Service
public class FileService {
  private final FileMapper fileMapper;
  private final HashingService hashingService;
  private final FileRepository fileRepository;

  public FileService(FileMapper fileMapper, HashingService hashingService, FileRepository fileRepository) {
    this.fileMapper = fileMapper;
    this.hashingService = hashingService;
    this.fileRepository = fileRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File createFile(MultipartFile multipartFile, ChunkingConfig chunkingConfig) {
    File file = fileMapper.splitInputToEntity(multipartFile, chunkingConfig);

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
  public List<File> getAllFile(FileFilterDto filters) {
    return fileRepository.findAll(FileSpecification.withFilters(filters));
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File updateFile(File file) {
    return fileRepository.save(file);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File getFileById(Long fileId) {
    return fileRepository.findById(fileId)
        .orElseThrow(() -> new EntityNotFoundException("File", "id", fileId.toString()));
  }
}
