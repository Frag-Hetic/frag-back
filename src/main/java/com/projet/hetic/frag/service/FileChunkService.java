package com.projet.hetic.frag.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.model.FileChunk;
import com.projet.hetic.frag.repository.FileChunkRepository;

@Service
public class FileChunkService {
  private final FileChunkRepository fileChunkRepository;

  public FileChunkService(FileChunkRepository fileChunkRepository) {
    this.fileChunkRepository = fileChunkRepository;

  }

  @Transactional(propagation = Propagation.REQUIRED)
  public FileChunk createFileChunk(File file, Chunk chunk, int order, long offsetStart) {
    FileChunk fileChunk = new FileChunk();
    fileChunk.setFile(file);
    fileChunk.setChunk(chunk);
    fileChunk.setChunkOrder(order);
    fileChunk.setOffsetStart(offsetStart);
    return fileChunkRepository.save(fileChunk);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public List<FileChunk> getFileChunkByFile(Long fileId) {
    List<FileChunk> fileChunks = fileChunkRepository.findByFileIdOrderByChunkOrderAsc(fileId);
    if (fileChunks.isEmpty()) {
      return null;
    }
    return fileChunks;
  }

}
