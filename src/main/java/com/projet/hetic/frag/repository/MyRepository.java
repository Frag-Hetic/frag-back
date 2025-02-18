package com.projet.hetic.frag.repository;

import com.projet.hetic.frag.service.MyEntity;
import org.springframework.data.repository.CrudRepository;

public interface MyRepository extends CrudRepository<MyEntity, Integer> {
}
