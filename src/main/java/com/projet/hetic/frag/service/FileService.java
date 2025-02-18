package com.projet.hetic.frag.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projet.hetic.frag.model.File;

@Service
public class FileService {
  public File createFile(MultipartFile multipartFile) {
    File file = new File();
    return file;
  }
}
