package com.projet.hetic.frag.service;

import com.projet.hetic.frag.service.MyEntity;
import com.projet.hetic.frag.repository.MyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // La transaction est annulée à la fin du test
public class MyServiceIntegrationTest {

    @Autowired
    private MyService myService; // Service qui gère la logique métier

    @Autowired
    private MyRepository myRepository; // Repository pour l'accès à la base de données

    private MyEntity entity;

    @BeforeEach
    public void setUp() {
        // Création d'une nouvelle entité avant chaque test
        entity = new MyEntity(0, "Test Entity");
    }

    @Test
    public void testSaveEntity() {
        // Sauvegarde de l'entité
        MyEntity savedEntity = myService.saveEntity(entity);

        // Vérification que l'entité a bien été sauvegardée
        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isGreaterThan(0); // L'id doit être généré
        assertThat(savedEntity.getName()).isEqualTo("Test Entity");
    }

    @Test
    public void testUpdateEntity() {
        // Sauvegarde initiale
        MyEntity savedEntity = myService.saveEntity(entity);

        // Mise à jour du nom de l'entité
        savedEntity.setName("Updated Name");
        MyEntity updatedEntity = myService.saveEntity(savedEntity);

        // Vérification que l'entité a bien été mise à jour
        assertThat(updatedEntity.getName()).isEqualTo("Updated Name");
        assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId()); // Le même id doit être retourné
    }

    @Test
    public void testFindEntityById() {
        // Sauvegarde de l'entité
        MyEntity savedEntity = myService.saveEntity(entity);

        // Recherche de l'entité par son id
        MyEntity foundEntity = myRepository.findById(savedEntity.getId()).orElse(null);

        // Vérification que l'entité retrouvée est bien celle enregistrée
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(foundEntity.getName()).isEqualTo(savedEntity.getName());
    }
}
