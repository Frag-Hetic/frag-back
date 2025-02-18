package com.projet.hetic.frag.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projet.hetic.frag.model.Chunk;
import com.projet.hetic.frag.repository.ChunkRepository;

@Service
public class HashingService {

  private final ChunkRepository chunkRepository;

  public HashingService(ChunkRepository chunkRepository) {
    this.chunkRepository = chunkRepository;
  }

  public String calculateSHA256(byte[] chunk) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(chunk);
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        hexString.append(String.format("%02x", b));
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }

  public boolean isDuplicate(byte[] chunk) {
    String hash = calculateSHA256(chunk);
    Optional<Chunk> existingChunk = chunkRepository.findByHash(hash);
    return existingChunk.isPresent();
  }
}