package com.projet.hetic.frag.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.hetic.frag.model.File;

public interface FileRepository extends JpaRepository<File, Long> {
}
