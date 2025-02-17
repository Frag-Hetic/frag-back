package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Files_Chunks")
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

  private Integer chunkOrder;
  private Long offsetStart;
}
