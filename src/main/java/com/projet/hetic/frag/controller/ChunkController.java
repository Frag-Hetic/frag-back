package com.projet.hetic.frag.controller;

import com.projet.hetic.frag.dto.ChunkDTO;
import com.projet.hetic.frag.service.ChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/chunks")
@RequiredArgsConstructor
public class ChunkController {
    private final ChunkService chunkService;

    /**
     * Récupérer un chunk spécifique par son hash
     */
    @GetMapping("/{hash}")
    public ResponseEntity<ChunkDTO> getChunkByHash(@PathVariable String hash) {
        Optional<ChunkDTO> chunk = chunkService.getChunkByHash(hash);
        return chunk.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
