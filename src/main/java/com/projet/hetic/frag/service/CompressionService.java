package com.projet.hetic.frag.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Service
public class CompressionService {

    /**
     * Compresse un chunk en utilisant ZLIB (Deflater).
     */
    public byte[] compressChunk(byte[] chunk) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream)) {

            deflaterOutputStream.write(chunk);
            deflaterOutputStream.close(); // Ferme pour s'assurer que tout est bien écrit

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la compression du chunk", e);
        }
    }

    /**
     * Décompresse un chunk compressé avec ZLIB.
     */
    public byte[] decompressChunk(byte[] compressedChunk) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedChunk);
             InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            inflaterInputStream.transferTo(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la décompression du chunk", e);
        }
    }
}
