package com.projet.hetic.frag.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.service.FileService;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/split")
    public ResponseEntity<File> splitFile(@RequestParam("file") MultipartFile multipartFile) {
        File file = fileService.createFile(multipartFile);
        return ResponseEntity.ok(file);
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
