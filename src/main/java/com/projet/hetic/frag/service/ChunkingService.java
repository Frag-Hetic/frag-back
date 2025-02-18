package com.projet.hetic.frag.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.stream.Stream;

import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;

public class ChunkingService {

  private static final int CHUNK_SIZE = 1024 * 8; // Taille approximative de 8KB
  private static final int POLYNOMIAL = 5; // Polynôme utilisé pour l'algorithme Rabin

  public Stream<byte[]> chunkFile(InputStream inputStream) {
    return Stream.generate(() -> {
      RabinFingerprintLongWindowed rabin = new RabinFingerprintLongWindowed(
          Polynomial.createIrreducible(POLYNOMIAL), 3); // Crée l'empreinte Rabin avec une fenêtre de taille 3
      ByteArrayOutputStream currentChunk = new ByteArrayOutputStream();
      try {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead;

        // Lecture des données à partir du fichier
        while ((bytesRead = bis.read(buffer)) != -1) {
          for (int i = 0; i < bytesRead; i++) {
            byte b = buffer[i];
            rabin.pushByte(b); // Met à jour l'empreinte Rabin
            currentChunk.write(b); // Ajoute l'octet au chunk actuel

            // Détection de la frontière du chunk
            if (rabin.getFingerprintLong() % CHUNK_SIZE == 0) {
              byte[] chunk = currentChunk.toByteArray();
              currentChunk.reset(); // Réinitialise le buffer pour le prochain chunk
              return chunk; // Retourne le chunk immédiatement
            }
          }
        }

        // Si on a des données restantes, retourner un dernier chunk
        if (currentChunk.size() > 0) {
          byte[] finalChunk = currentChunk.toByteArray();
          currentChunk.reset();
          return finalChunk;
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null; // Fin du flux
    }).takeWhile(chunk -> chunk != null); // Arrêter quand il n'y a plus de chunks
  }
}
