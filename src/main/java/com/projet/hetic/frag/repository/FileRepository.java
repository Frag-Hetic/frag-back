package com.projet.hetic.frag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.projet.hetic.frag.model.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {

        @EntityGraph(attributePaths = { "filesChunks.chunk" })
        @NonNull
        Optional<File> findById(@NonNull Long id);
}
