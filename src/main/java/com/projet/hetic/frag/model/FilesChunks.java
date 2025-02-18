package com.projet.hetic.frag.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "files_chunks", indexes = {
    @Index(name = "idx_file_id_chunk_order", columnList = "file_id, chunk_order"),
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_file_id_chunk_order", columnNames = { "file_id", "chunk_order" }),
})
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

  @Column(nullable = false, name = "chunk_order")
  private Integer chunkOrder;

  @Column(nullable = false, name = "offset_start")
  private Long offsetStart;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
