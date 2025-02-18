package com.projet.hetic.frag.dto;

public record ChunkDTO(
        Long id,
        String hash,
        Integer sizeOriginal,
        Integer sizeCompressed
) {}
