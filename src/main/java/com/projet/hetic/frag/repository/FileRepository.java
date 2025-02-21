package com.projet.hetic.frag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.projet.hetic.frag.model.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

  @EntityGraph(attributePaths = { "filesChunks.chunk" })
  @NonNull
  Optional<File> findById(@NonNull Long id);

  @Query("SELECT f FROM File f WHERE " +
      "(:fileName IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :fileName, '%'))) AND " +
      "(:mimeType IS NULL OR f.mimeType = :mimeType)")
  List<File> findAllWithFilters(
      @Param("fileName") String fileName,
      @Param("mimeType") String mimeType);
}
