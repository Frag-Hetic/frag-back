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

  @Column(nullable = false)
  private Integer sizeOriginal;

  @Column(nullable = false)
  private Integer sizeCompressed;

  @Column(nullable = false)
  @Lob
  private byte[] data;

  @Column(length = 10, nullable = false)
  private String compressionType;

  @OneToMany(mappedBy = "chunk", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FilesChunks> filesChunks;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
