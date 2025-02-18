package com.projet.hetic.frag.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompressionServiceTest {

    private final CompressionService compressionService = new CompressionService();

    @Test
    void testCompressionAndDecompressionZlib() {
        // Données originales
        String testData = "  \n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam posuere luctus mauris, eu vehicula sapien varius et. In varius cursus hendrerit. Nam non mattis dolor, eu interdum lorem. Integer maximus feugiat tortor et efficitur. Integer suscipit, orci id eleifend laoreet, diam nisl eleifend lacus, luctus scelerisque massa libero vitae ex. Aenean vel turpis magna. Curabitur a lectus eu mauris tristique pharetra. Pellentesque auctor eleifend quam, tincidunt imperdiet velit consequat sed. Mauris iaculis purus quis purus condimentum sagittis. Phasellus vel faucibus metus. Sed tempus lorem vel malesuada vehicula. Aenean accumsan, justo ut interdum molestie, leo nulla interdum diam, ut rutrum libero lectus vitae libero. Phasellus dictum viverra dignissim. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Donec ullamcorper risus vitae urna facilisis, id facilisis risus cursus.\n" +
                "\n" +
                "Sed vitae laoreet turpis, at luctus velit. Pellentesque dapibus fringilla velit, nec mattis mi tincidunt sit amet. Pellentesque ac enim at turpis hendrerit luctus eu id dui. Duis aliquam neque ut diam volutpat, ut tempor tortor sollicitudin. Aenean fringilla mi ut nibh orci.";
        byte[] originalData = testData.getBytes();

        // Compression
        byte[] compressedData = compressionService.compressChunk(originalData);
        assertNotNull(compressedData, "La compression ZLIB a échoué");
        assertTrue(compressedData.length < originalData.length, "La taille compressée doit être inférieure à la taille originale");

        // Décompression
        byte[] decompressedData = compressionService.decompressChunk(compressedData);
        assertNotNull(decompressedData, "La décompression ZLIB a échoué");
        assertArrayEquals(originalData, decompressedData, "Les données décompressées ne correspondent pas aux originales");
    }
}
