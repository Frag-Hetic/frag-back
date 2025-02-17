package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Chunk")
@Data
public class Chunk {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 64, nullable = false)
  private String hash;

  private Integer sizeOriginal;
  private Integer sizeCompressed;

  @Lob
  private byte[] data;

  @Column(length = 10)
  private String compressionType;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
