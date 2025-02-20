package com.projet.hetic.frag.service;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.utils.ProcessingStats;
import com.projet.hetic.frag.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileChunkProcessor {
  private final FileService fileService;
  private final ChunkService chunkService;
  private final FileChunkService fileChunkService;
  private final ChunkingConfig defaultConfig;
  private final TimeUtils timeUtils;

  public File processFile(MultipartFile multipartFile, ChunkingParamsDto params) {
    try {
      ChunkingConfig config = createChunkingConfig(params);
      ChunkingService chunkingService = new ChunkingService(config);
      InputStream inputStream = multipartFile.getInputStream();
      File tempFile = fileService.createFile(multipartFile, config);
      long startTime = System.currentTimeMillis();
      processFileChunks(inputStream, chunkingService, tempFile, startTime);
      return fileService.updateFile(tempFile);
    } catch (Exception e) {
      throw new FileProcessingException(e.getMessage());
    }
  }

  private void processFileChunks(InputStream inputStream, ChunkingService chunkingService,
      File tempFile, long startTime) {
    var processingStats = new ProcessingStats();

    try {
      chunkingService.chunkFile(inputStream)
          .map(chunkService::findOrCreateChunk)
          .forEach(chunk -> processChunk(chunk, tempFile, processingStats));

      updateFileStats(tempFile, processingStats, startTime);
    } catch (Exception e) {
      throw new FileProcessingException("Erreur lors du traitement des chunks: " + e.getMessage());
    }
  }

  private void processChunk(Chunk chunk, File tempFile, ProcessingStats stats) {
    fileChunkService.createFileChunk(
        tempFile,
        chunk,
        stats.getOrder().get(),
        stats.getOffset().get());
    stats.updateStats(chunk);
  }

  public ChunkingConfig createChunkingConfig(ChunkingParamsDto params) {
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
    return tempConfig;
  }

  private void updateFileStats(File tempFile, ProcessingStats stats, long startTime) {
    long processingTime = System.currentTimeMillis() - startTime;
    tempFile.setCompressedFileSize(stats.getTotalCompressedSize().get());
    tempFile.setProcessingTime(timeUtils.getFormattedProcessingTime(processingTime));
  }
}
