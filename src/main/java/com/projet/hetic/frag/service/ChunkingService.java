package com.projet.hetic.frag.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class ChunkingService {
  private static final int WINDOW_SIZE = 48; // Taille de la fenêtre glissante
  private static final int CHUNK_MIN_SIZE = 1024; // Taille minimale du chunk
  private static final int CHUNK_MAX_SIZE = 8192; // Taille maximale du chunk
  private static final int BREAKPOINT_MASK = 0x1FFF; // Masque pour détecter un point de coupure

  public Stream<byte[]> chunkFile(InputStream inputStream) throws IOException {
    List<byte[]> chunks = new ArrayList<>();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] window = new byte[WINDOW_SIZE];
    int bytesRead;
    int windowIndex = 0;
    int rollingHash = 0;

    while ((bytesRead = inputStream.read()) != -1) {
      // Ajouter l'octet au buffer
      buffer.write(bytesRead);

      // Gérer la fenêtre glissante
      rollingHash = ((rollingHash << 1) + bytesRead) & 0xFFFF;
      if (buffer.size() > WINDOW_SIZE) {
        rollingHash -= window[windowIndex];
      }
      window[windowIndex] = (byte) bytesRead;
      windowIndex = (windowIndex + 1) % WINDOW_SIZE;

      // Vérifier les conditions de découpage
      if ((rollingHash & BREAKPOINT_MASK) == 0 && buffer.size() >= CHUNK_MIN_SIZE || buffer.size() >= CHUNK_MAX_SIZE) {
        chunks.add(buffer.toByteArray());
        buffer.reset();
      }
    }

    // Ajouter le dernier chunk s'il reste des données
    if (buffer.size() > 0) {
      chunks.add(buffer.toByteArray());
    }

    return chunks.stream();
  }
}