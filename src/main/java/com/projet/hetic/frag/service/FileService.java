package com.projet.hetic.frag.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.repository.FileRepository;

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

  public List<File> getAllFile() {
    return fileRepository.findAll();
  }
}
