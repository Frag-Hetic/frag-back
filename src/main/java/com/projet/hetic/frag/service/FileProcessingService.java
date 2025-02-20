package com.projet.hetic.frag.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;

@Service
public class FileProcessingService {
  private final FileService fileService;
  private final FileChunkService fileChunkService;
  private final FileConstructionService fileConstructionService;
  private final HashingService hashingService;
  private final FileMapper fileMapper;
  private final FileChunkProcessor fileChunkProcessor; // Configuration par défaut

  public FileProcessingService(FileService fileService,
      FileChunkService fileChunkService,
      HashingService hashingService, FileMapper fileMapper, FileConstructionService fileConstructionService,
      FileChunkProcessor fileChunkProcessor) {
    this.fileService = fileService;
    this.fileChunkService = fileChunkService;
    this.fileConstructionService = fileConstructionService;
    this.hashingService = hashingService;
    this.fileMapper = fileMapper;
    this.fileChunkProcessor = fileChunkProcessor;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File processAndSplitFile(MultipartFile multipartFile, ChunkingParamsDto params) {
    return fileChunkProcessor.processFile(multipartFile, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public FileDownloadDTO processAndUnsplitFile(Long fileId) {
    try {
      File file = fileService.getFileById(fileId);
      List<FileChunk> fileChunks = fileChunkService.getFileChunkByFile(fileId);

      if (fileChunks.isEmpty()) {
        byte[] fileContentBytes = {};
        return fileMapper.toDownloadDTO(file, fileContentBytes);
      }

      byte[] fileContentBytes = fileConstructionService.reconstructFileFromChunks(fileChunks, fileId);
      if (!hashingService.compareConstructFileWithCheckHash(fileContentBytes, file.getCheckhash())) {
        throw new RuntimeException("Hashes do not match");
      }

      return fileMapper.toDownloadDTO(file, fileContentBytes);
    } catch (RuntimeException e) {
      throw new FileProcessingException("Fail unsplit: " + e.getMessage());
    }
  }
}
