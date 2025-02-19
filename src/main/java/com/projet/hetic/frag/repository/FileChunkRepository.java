package com.projet.hetic.frag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet.hetic.frag.model.FileChunk;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunk, Long> {
  List<FileChunk> findByFileIdOrderByChunkOrderAsc(Long fileId);
}
