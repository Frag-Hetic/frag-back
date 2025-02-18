package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "chunk")
@Data
public class Chunk {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 64, nullable = false, unique = true)
  private String hash;

  @Column(nullable = false, name = "size_original")
  private Integer sizeOriginal;

  @Column(nullable = false, name = "size_compressed")
  private Integer sizeCompressed;

  @Column(nullable = false)
  @Lob
  private byte[] data;

  @Column(length = 10, nullable = false, name = "compression_type")
  private String compressionType;

  @OneToMany(mappedBy = "chunk", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FilesChunks> filesChunks;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
