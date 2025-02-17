package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "File")
@Data
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String filename;
  private Long fileSize;

  @Column(name = "mime_t", length = 150, unique = true)
  private String mimeType;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Column(length = 64)
  private String checkhash;

  @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FilesChunks> filesChunks;
}
