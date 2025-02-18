package com.projet.hetic.frag.mapper;

//import com.projet.hetic.frag.dto.FileResponseDTO;
//import com.projet.hetic.frag.model.File;
import org.springframework.stereotype.Component;

/**
 * Convertit des entités JPA en DTOs et inversement.
 */
@Component
public class FileMapper {

    /**
     * Convertit une entité File en DTO.
     * @param file Entité File
     * @return DTO correspondant
     */
//    public FileResponseDTO toDto(File file) {
//        return new FileResponseDTO(
//                file.getId(),
//                file.getFilename(),
//                file.getFileSize(),
//                file.getMimeType()
//        );
//    }

    /**
     * Convertit un DTO en entité File (sans id ni relation avec les chunks).
     * @param dto DTO FileResponseDTO
     * @return Entité File
     */
//    public File toEntity(FileResponseDTO dto) {
//        return new File(null, dto.filename(), dto.size(), dto.mimeType(), null);
//    }
}
