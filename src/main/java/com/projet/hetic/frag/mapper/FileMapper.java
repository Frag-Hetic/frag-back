package com.projet.hetic.frag.mapper;

import com.projet.hetic.frag.config.ChunkingConfig;
import com.projet.hetic.frag.dto.FileDownloadDTO;
import com.projet.hetic.frag.model.File;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Convertit des entités JPA en DTOs et inversement.
 */
@Component
public class FileMapper {
    /**
     * Convertit un MultipartFile en entité FileInputDto
     * 
     * @param multipartFile
     * @return Entité FileInputDto without hash
     */
    public File multipartToEntity(MultipartFile multipartFile) {
        File file = new File();
        file.setFileSize(multipartFile.getSize());
        file.setCompressedFileSize(0L);
        file.setFilename(multipartFile.getOriginalFilename());
        file.setMimeType(multipartFile.getContentType());
        return file;
    }

    public File splitInputToEntity(MultipartFile multipartFile, ChunkingConfig chunkingConfig) {
        File file = new File();
        file.setFileSize(multipartFile.getSize());
        file.setCompressedFileSize(0L);
        file.setFilename(multipartFile.getOriginalFilename());
        file.setMimeType(multipartFile.getContentType());
        file.setWindowSize(chunkingConfig.getWindowSize());
        file.setChunkMinSize(chunkingConfig.getChunkMinSize());
        file.setChunkMaxSize(chunkingConfig.getChunkMaxSize());
        file.setBreakpointMask(chunkingConfig.getBreakpointMask());
        return file;
    }

    public FileDownloadDTO toDownloadDTO(File file, byte[] fileContent) {
        FileDownloadDTO fileDownloadDTO = new FileDownloadDTO();
        fileDownloadDTO.setFilename(file.getFilename());
        fileDownloadDTO.setMimeType(file.getMimeType());
        fileDownloadDTO.setFileContent(fileContent);
        return fileDownloadDTO;
    }
}
