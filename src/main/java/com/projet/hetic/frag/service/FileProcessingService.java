package com.projet.hetic.frag.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;
import com.projet.hetic.frag.utils.TimeUtils;

@Service
public class FileProcessingService {
  private final FileService fileService;
  private final ChunkService chunkService;
  private final FileChunkService fileChunkService;
  private final FileConstructionService fileConstructionService;
  private final HashingService hashingService;
  private final FileMapper fileMapper;
  private final ChunkingConfig defaultConfig; // Configuration par défaut
  private final TimeUtils timeUtils; // Configuration par défaut

  public FileProcessingService(FileService fileService,
      ChunkService chunkService, FileChunkService fileChunkService,
      HashingService hashingService, FileMapper fileMapper, FileConstructionService fileConstructionService,
      ChunkingConfig defaultConfig, TimeUtils timeUtils) {
    this.fileService = fileService;
    this.chunkService = chunkService;
    this.fileChunkService = fileChunkService;
    this.fileConstructionService = fileConstructionService;
    this.hashingService = hashingService;
    this.fileMapper = fileMapper;
    this.defaultConfig = defaultConfig;
    this.timeUtils = timeUtils;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File processAndSplitFile(MultipartFile multipartFile, ChunkingParamsDto params) {

    // Créer une configuration temporaire basée sur les paramètres
    ChunkingConfig tempConfig = new ChunkingConfig();
    if (params != null) {
      tempConfig.setWindowSize(params.getWindowSize());
      tempConfig.setChunkMinSize(params.getChunkMinSize());
      tempConfig.setChunkMaxSize(params.getChunkMaxSize());
      tempConfig.setBreakpointMask(params.getBreakpointMask());
    } else {
      tempConfig = defaultConfig;
    }

    System.out.println("tempConfig: " + tempConfig);

    ChunkingService configuredChunkingService = new ChunkingService(tempConfig);

    File tempFile = fileService.createFile(multipartFile, tempConfig);

    // Démarrer le timer
    long startTime = System.currentTimeMillis();

    try {
      AtomicInteger order = new AtomicInteger(0);
      AtomicInteger offsetStart = new AtomicInteger(0);
      AtomicInteger totalSizeCompressed = new AtomicInteger(0);
      InputStream inputStream = multipartFile.getInputStream();
      Stream<byte[]> chunks = configuredChunkingService.chunkFile(inputStream);
      chunks.map(chunkService::findOrCreateChunk).forEach(chunk -> {
        fileChunkService.createFileChunk(tempFile, chunk, order.get(), offsetStart.get());
        order.getAndIncrement();
        offsetStart.addAndGet(chunk.getSizeOriginal());
        totalSizeCompressed.addAndGet(chunk.getSizeCompressed());
      });
      // Calculer le temps écoulé
      long processingTime = System.currentTimeMillis() - startTime;

      tempFile.setCompressedFileSize(totalSizeCompressed.longValue());
      tempFile.setProcessingTime(timeUtils.getFormattedProcessingTime(processingTime));

      return fileService.updateFile(tempFile);
    } catch (IOException e) {
      throw new FileProcessingException("Fail split: " + e.getMessage());
    }
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
