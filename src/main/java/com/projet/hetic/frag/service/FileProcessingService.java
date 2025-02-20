package com.projet.hetic.frag.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.projet.hetic.frag.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;

@Service
public class FileProcessingService {
  private final FileService fileService;
  private final ChunkingService chunkingService;
  private final ChunkService chunkService;
  private final FileChunkService fileChunkService;
  private final FileConstructionService fileConstructionService;
  private final HashingService hashingService;
  private final FileMapper fileMapper;

  public FileProcessingService(FileService fileService,
      ChunkingService chunkingService,
      ChunkService chunkService, FileChunkService fileChunkService,
      HashingService hashingService, FileMapper fileMapper, FileConstructionService fileConstructionService) {
    this.fileService = fileService;
    this.chunkingService = chunkingService;
    this.chunkService = chunkService;
    this.fileChunkService = fileChunkService;
    this.fileConstructionService = fileConstructionService;
    this.hashingService = hashingService;
    this.fileMapper = fileMapper;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public File processAndSplitFile(MultipartFile multipartFile) {
    File tempFile = fileService.createFile(multipartFile);

    try {
      AtomicInteger order = new AtomicInteger(0);
      AtomicInteger offsetStart = new AtomicInteger(0);
      AtomicInteger totalSizeCompressed = new AtomicInteger(0);
      InputStream inputStream = multipartFile.getInputStream();
      Stream<byte[]> chunks = chunkingService.chunkFile(inputStream);
      chunks.map(chunkService::findOrCreateChunk).forEach(chunk -> {
        fileChunkService.createFileChunk(tempFile, chunk, order.get(), offsetStart.get());
        order.getAndIncrement();
        offsetStart.addAndGet(chunk.getSizeOriginal());
        totalSizeCompressed.addAndGet(chunk.getSizeCompressed());
      });
      tempFile.setCompressedFileSize(totalSizeCompressed.longValue());
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
