package com.projet.hetic.frag.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.exception.FileProcessingException;
import com.projet.hetic.frag.mapper.FileMapper;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileReconstructionProcessor {
  private final FileService fileService;
  private final FileChunkService fileChunkService;
  private final FileConstructionService fileConstructionService;
  private final HashingService hashingService;
  private final FileMapper fileMapper;

  public FileDownloadDTO reconstructFile(Long fileId) {
    File file = fileService.getFileById(fileId);
    List<FileChunk> fileChunks = fileChunkService.getFileChunkByFile(fileId);

    if (fileChunks.isEmpty()) {
      return fileMapper.toDownloadDTO(file, new byte[] {});
    }

    byte[] reconstructedContent = reconstructAndVerify(file, fileChunks);
    return fileMapper.toDownloadDTO(file, reconstructedContent);
  }

  private byte[] reconstructAndVerify(File file, List<FileChunk> chunks) {
    byte[] content = fileConstructionService.reconstructFileFromChunks(chunks, file.getId());
    if (!hashingService.compareConstructFileWithCheckHash(content, file.getCheckhash())) {
      throw new FileProcessingException("Hash verification failed");
    }
    return content;
  }
}
