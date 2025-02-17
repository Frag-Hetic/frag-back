package com.projet.hetic.frag.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "file")
@Data
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String filename;

  @Column(nullable = false)
  private Long fileSize;

  @Column(name = "mime_type", length = 150, unique = true, nullable = false)
  private String mimeType;

  @Column(length = 64, nullable = false)
  private String checkhash;

  @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FilesChunks> filesChunks;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
