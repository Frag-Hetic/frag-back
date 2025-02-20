package com.projet.hetic.frag.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.rabinfingerprint.fingerprint.RabinFingerprintLongWindowed;
import org.rabinfingerprint.polynomial.Polynomial;
import org.springframework.stereotype.Service;

@Service
public class ChunkingService {

  private static final Long POLYNOMIAL = 5L; // Polynôme utilisé pour l'algorithme Rabin

  public Stream<byte[]> chunkFile(InputStream input) {
    // 1. Créer un polynôme irréductible (53 bits)
    Polynomial polynomial = Polynomial.createFromLong(POLYNOMIAL);

    // 2. Créer une fenêtre glissante de 48 octets
    RabinFingerprintLongWindowed window = new RabinFingerprintLongWindowed(polynomial, 48);

    // 3. Stocker les segments découpés
    List<byte[]> segments = new ArrayList<>();

    try (ByteArrayOutputStream segmentBuffer = new ByteArrayOutputStream()) {
      int b;
      while ((b = input.read()) != -1) {
        // Ajouter l'octet au segment actuel
        segmentBuffer.write(b);
        window.pushByte((byte) b);

        // Vérifier la condition de découpage (empreinte divisible par
        // chunkBoundaryCondition)
        if (window.getFingerprintLong() % 4 == 0) {
          // Ajouter le segment découpé à la liste
          segments.add(segmentBuffer.toByteArray());
          // System.out.println("Segment -> " + new String(segmentBuffer.toByteArray()));
          // System.out.println("Segment printing -> " + window.getFingerprintLong());
          // Réinitialiser le buffer pour le prochain segment
          segmentBuffer.reset();
        }
      }

      // Ajouter le dernier segment si des données restent dans le buffer
      if (segmentBuffer.size() > 0) {
        segments.add(segmentBuffer.toByteArray());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 4. Retourner les segments sous forme de Stream<byte[]>
    return segments.stream();
  }
}
