package com.projet.hetic.frag.specification;

import org.springframework.data.jpa.domain.Specification;
import com.projet.hetic.frag.model.File;
import com.projet.hetic.frag.dto.FileFilterDto;

public class FileSpecification {

  public static Specification<File> withFilters(FileFilterDto filters) {
    return Specification.where(withFileName(filters.getFileName()))
        .and(withMimeType(filters.getMimeType()));
  }

  private static Specification<File> withFileName(String fileName) {
    return (root, query, cb) -> {
      if (fileName == null) {
        return null;
      }
      return cb.like(root.get("fileName"), "%" + fileName + "%");
    };
  }

  private static Specification<File> withMimeType(String mimeType) {
    return (root, query, cb) -> {
      if (mimeType == null) {
        return null;
      }
      return cb.equal(root.get("mimeType"), mimeType);
    };
  }
}