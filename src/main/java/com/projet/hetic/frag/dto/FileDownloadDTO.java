package com.projet.hetic.frag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadDTO {
        private String filename;
        private String mimeType;
        private byte[] fileContent;
}