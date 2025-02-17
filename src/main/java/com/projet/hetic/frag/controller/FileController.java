package com.projet.hetic.frag.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
    @PostMapping("/split")
    public ResponseEntity<String> splitFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(file.getOriginalFilename());
    }

    @GetMapping("/chunks")
    public ResponseEntity<String> getChunks(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/unsplit/{fileId}")
    public ResponseEntity<String> unsplitFile(@PathVariable String fileId) {
        return ResponseEntity.ok("supp");
    }

}
