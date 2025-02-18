package com.projet.hetic.frag.dto;

public record FileDownloadDTO(
        String filename,
        byte[] content,
        String mimeType
) {}