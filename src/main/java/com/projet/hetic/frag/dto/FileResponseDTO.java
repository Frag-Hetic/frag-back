package com.projet.hetic.frag.dto;

public record FileResponseDTO(
        Long id,
        String filename,
        Long size,
        String mimeType
) {}
