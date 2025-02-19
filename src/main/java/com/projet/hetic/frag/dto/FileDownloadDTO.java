package com.projet.hetic.frag.dto;

public class FileDownloadDTO {
        private String filename;
        private String mimeType;
        private byte[] fileContent;

        public FileDownloadDTO(String filename, String mimeType, byte[] fileContent) {
                this.filename = filename;
                this.mimeType = mimeType;
                this.fileContent = fileContent;
        }

        public String getFilename() {
                return filename;
        }

        public String getMimeType() {
                return mimeType;
        }

        public byte[] getFileContent() {
                return fileContent;
        }
}