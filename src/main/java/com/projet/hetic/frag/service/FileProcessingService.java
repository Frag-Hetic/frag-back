package com.projet.hetic.frag.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.model.File;

@Service
public class FileProcessingService {

  private final FileChunkProcessor fileChunkProcessor; // Configuration par défaut
  private final FileReconstructionProcessor fileReconstructionProcessor; // Configuration par défaut

  public FileProcessingService(
      FileChunkProcessor fileChunkProcessor, FileReconstructionProcessor fileReconstructionProcessor) {
    this.fileChunkProcessor = fileChunkProcessor;
    this.fileReconstructionProcessor = fileReconstructionProcessor;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File processAndSplitFile(MultipartFile multipartFile, ChunkingParamsDto params) {
    return fileChunkProcessor.processFile(multipartFile, params);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public FileDownloadDTO processAndUnsplitFile(Long fileId) {
    return fileReconstructionProcessor.reconstructFile(fileId);
  }
}
