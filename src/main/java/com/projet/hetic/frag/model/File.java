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

  @Column(nullable = false, name = "file_name")
  private String filename;

  @Column(nullable = false, name = "file_size")
  private Long fileSize;

  @Column(name = "mime_type", length = 150, unique = true, nullable = false)
  private String mimeType;

  @Column(length = 64, nullable = false, name = "check_hash")
  private String checkhash;

  @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FilesChunks> filesChunks;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
