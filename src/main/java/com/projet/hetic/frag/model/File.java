package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "file")
@Data
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, name = "file_name")
  private String filename;

  @Column(nullable = false, name = "file_size")
  private Long fileSize;

  @Column(nullable = false, name = "compressed_file_size")
  private Long compressedFileSize;

  @Column(name = "mime_type", length = 150, nullable = false)
  private String mimeType;

  @Column(name = "window_size", nullable = false)
  private Integer windowSize;

  @Column(name = "chunk_min_size", nullable = false)
  private Integer chunkMinSize;

  @Column(name = "chunk_max_size", nullable = false)
  private Integer chunkMaxSize;

  @Column(name = "breakpoint_mask", length = 150, nullable = false)
  private String breakpointMask;

  @Column(name = "processing_time")
  private String processingTime;

  @Column(length = 64, nullable = false, name = "check_hash")
  private String checkhash;

  @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<FileChunk> filesChunks;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
