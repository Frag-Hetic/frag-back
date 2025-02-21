package com.projet.hetic.frag.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.dto.ChunkingParamsDto;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.dto.FileFilterDto;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.service.FileProcessingService;
import com.projet.hetic.frag.service.FileService;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;

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
    public ResponseEntity<List<File>> getFiles(@ModelAttribute FileFilterDto filters) {
        List<File> files = fileService.getAllFile(filters);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable Long id) {
        return ResponseEntity.ok(
                fileService.getFileById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable Long id) {
        fileService.deleteFileById(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "File deleted successfully"));
    }

    @PostMapping("/split")
    public ResponseEntity<File> splitFile(@RequestParam("file") MultipartFile multipartFile,
            @ModelAttribute @Valid ChunkingParamsDto params) {
        File file = fileProcessingService.processAndSplitFile(multipartFile, params);
        URI location = URI.create(String.format("/files/%d", file.getId()));
        return ResponseEntity.created(location).body(file);
    }

    @GetMapping("/unsplit/{fileId}")
    public ResponseEntity<byte[]> unsplitFile(@PathVariable Long fileId) {
        FileDownloadDTO file = fileProcessingService.processAndUnsplitFile(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getMimeType()));
        try {
            String encodedFilename = URLEncoder.encode(file.getFilename(), StandardCharsets.UTF_8.toString());
            headers.setContentDispositionFormData("attachment", encodedFilename); // Use "inline" for display
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to encode filename", e);
        }
        return new ResponseEntity<>(file.getFileContent(), headers, HttpStatus.OK);
    }

}
