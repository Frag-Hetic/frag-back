package com.projet.hetic.frag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet.hetic.frag.model.Chunk;

import java.util.Optional;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, Long> {
  Optional<Chunk> findByHash(String hash);
}