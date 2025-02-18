package com.projet.hetic.frag.mapper;

import com.projet.hetic.frag.dto.FileIntputDto;
//import com.projet.hetic.frag.dto.FileResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Convertit des entités JPA en DTOs et inversement.
 */
@Component
public class FileMapper {

    /**
     * Convertit une entité File en DTO.
     * 
     * @param file Entité File
     * @return DTO correspondant
     */
    // public FileResponseDTO toDto(File file) {
    // return new FileResponseDTO(
    // file.getId(),
    // file.getFilename(),
    // file.getFileSize(),
    // file.getMimeType()
    // );
    // }

    /**
     * Convertit un MultipartFile en entité FileInputDto
     * 
     * @param multipartFile
     * @return Entité FileInputDto without hash
     */
    public FileIntputDto multipartToInputDto(MultipartFile multipartFile) {
        FileIntputDto fileInputDto = new FileIntputDto();
        fileInputDto.setFileSize(multipartFile.getSize());
        fileInputDto.setFilename(multipartFile.getOriginalFilename());
        fileInputDto.setMimeType(multipartFile.getContentType());
        return fileInputDto;
    }
}
