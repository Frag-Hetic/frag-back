package com.projet.hetic.frag.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "files_chunks")
@Data
public class FilesChunks {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "file_id", nullable = false)
  private File file;

  @ManyToOne
  @JoinColumn(name = "chunk_id", nullable = false)
  private Chunk chunk;

  @Column(nullable = false)
  private Integer chunkOrder;

  @Column(nullable = false)
  private Long offsetStart;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
