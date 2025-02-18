package com.projet.hetic.frag.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.FileIntputDto;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;

@Service
public class FileService {
  private final FileMapper fileMapper;
  private final HashingService hashingService;

  public FileService(FileMapper fileMapper, HashingService hashingService) {
    this.fileMapper = fileMapper;
    this.hashingService = hashingService;
  }

  public File createFile(MultipartFile multipartFile) {
    FileIntputDto fileInpuDdto = fileMapper.multipartToInputDto(multipartFile);

    byte[] bytes;
    try {
      bytes = multipartFile.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read multipart file", e);
    }

    String hash = hashingService.calculateSHA256(bytes);
    fileInpuDdto.setCheckhash(hash);

    File file = new File();
    return file;
  }
}
