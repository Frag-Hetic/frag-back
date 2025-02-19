package com.projet.hetic.frag.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.service.FileProcessingService;
import com.projet.hetic.frag.service.FileService;

@RestController
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;
    private final FileProcessingService fileProcessingService;

    public FileController(FileService fileService, FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseEntity<List<File>> getFiles() {
        List<File> files = fileService.getAllFile();
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable Long id) {
        return ResponseEntity.ok(
                fileService.getFileById(id));
    }

    @PostMapping("/split")
    public ResponseEntity<File> splitFile(@RequestParam("file") MultipartFile multipartFile) {
        File file = fileProcessingService.processAndSplitFile(multipartFile);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/chunks")
    public ResponseEntity<String> getChunks(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/unsplit/{id}")
    public ResponseEntity<String> unsplitFile(@PathVariable String id) {
        return ResponseEntity.ok("supp");
    }

}
