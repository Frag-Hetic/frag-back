package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "File")
@Data
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String filename;
  private Long fileSize;

  @Column(name = "mime_t", length = 150)
  private String mimeType;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Column(length = 64)
  private String checksum;
}
