package com.projet.hetic.frag.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;

public class ChunkingService {
  private static final int CHUNK_SIZE = 1024 * 8; // Taille de chunk approximative (8KB)
  private static final int POLYNOMIAL = 5; // Exemple de polynôme

  public Stream<byte[]> chunkFile(InputStream inputStream) {
    List<byte[]> chunks = new ArrayList<>();
    RabinFingerprintLongWindowed rabin = new RabinFingerprintLongWindowed(
        Polynomial.createIrreducible(POLYNOMIAL), 3);

    try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
      byte[] buffer = new byte[CHUNK_SIZE];
      ByteArrayOutputStream currentChunk = new ByteArrayOutputStream();
      int bytesRead;

      // Lecture des données et détection des frontières
      while ((bytesRead = bis.read(buffer)) != -1) {
        for (int i = 0; i < bytesRead; i++) {
          byte b = buffer[i];
          rabin.pushByte(b); // Mettre à jour le hachage Rabin

          // Ajouter l'octet au chunk actuel
          currentChunk.write(b);

          // Détecter la frontière basée sur le hachage
          if (rabin.getFingerprintLong() % CHUNK_SIZE == 0) {
            // Cloner le chunk actuel et l'ajouter à la liste des chunks
            chunks.add(currentChunk.toByteArray());
            currentChunk.reset(); // Réinitialiser pour le prochain chunk
          }
        }
      }

      // Ajouter les données restantes (si non terminées à une frontière)
      if (currentChunk.size() > 0) {
        chunks.add(currentChunk.toByteArray());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return chunks.stream();
  }

}