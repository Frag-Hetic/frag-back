package com.projet.hetic.frag.service;

import com.projet.hetic.frag.repository.MyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MyService {

    private final MyRepository myRepository;

    @Autowired
    public MyService(MyRepository myRepository) {
        this.myRepository = myRepository;
    }

    public MyEntity findEntityById(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
    
        Optional<MyEntity> entity = myRepository.findById(id);
        return entity.orElseThrow(() -> new RuntimeException("Entity not found"));
    }
    

    public MyEntity saveEntity(MyEntity entity) {
        return myRepository.save(entity); // Ajout de l'entité en base
    }

    public MyEntity updateEntity(int id, String newName) {
        MyEntity entity = myRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entity not found"));
        entity.setName(newName); // Modifier l'entité
        return myRepository.save(entity); // Sauvegarder les changements
    }
    
    
}
