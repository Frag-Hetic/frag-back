package com.projet.hetic.frag.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projet.hetic.frag.repository.MyRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Ajout de l'extension Mockito pour JUnit 5
public class MyServiceMockitoTest {

    @Mock
    private MyRepository myRepository; // Simulation du repository

    @InjectMocks
    private MyService myService; // Service à tester

    // Test de la recherche d'une entité par ID
    @Test
    public void testFindEntityById() {
        // Arrange
        MyEntity mockEntity = new MyEntity(1, "Test Entity");
        when(myRepository.findById(1)).thenReturn(Optional.of(mockEntity)); // Simuler une réponse du repository

        // Act
        MyEntity entity = myService.findEntityById(1); // Appeler la méthode du service

        // Assert
        assertEquals("Test Entity", entity.getName()); // Vérifier le résultat
    }

    // Test de la recherche d'une entité inexistante
    @Test
    public void testFindEntityById_EntityNotFound() {
        // Arrange
        when(myRepository.findById(1)).thenReturn(Optional.empty()); // Simuler une absence d'entité dans le repository

        // Act & Assert
        assertThrows(RuntimeException.class, () -> myService.findEntityById(1)); // Vérifier qu'une exception est levée
    }

    // Test de l'ajout d'une nouvelle entité
    @Test
    public void testSaveEntity() {
        // Given
        MyEntity entityToSave = new MyEntity(2, "New Entity");
        when(myRepository.save(any(MyEntity.class))).thenReturn(entityToSave); // Simuler l'enregistrement

        // When
        MyEntity savedEntity = myService.saveEntity(entityToSave);

        // Then
        assertNotNull(savedEntity);
        assertEquals(2, savedEntity.getId());
        assertEquals("New Entity", savedEntity.getName());

        verify(myRepository, times(1)).save(entityToSave); // Vérifier l'appel à save()
    }

    // Test de la mise à jour d'une entité
    @Test
    public void testUpdateEntity() {
        // Arrange
        MyEntity existingEntity = new MyEntity(1, "Old Name");
        when(myRepository.findById(1)).thenReturn(Optional.of(existingEntity)); // Simuler la récupération de l'entité
        when(myRepository.save(any(MyEntity.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Simuler l'enregistrement

        // Act
        MyEntity updatedEntity = myService.updateEntity(1, "New Name");

        // Assert
        assertEquals(1, updatedEntity.getId());
        assertEquals("New Name", updatedEntity.getName());
        verify(myRepository).save(any(MyEntity.class)); // Vérifier que save() a été appelé
    }

    // Test de la mise à jour d'une entité inexistante
    @Test
    public void testUpdateEntity_NotFound() {
        // Arrange
        when(myRepository.findById(1)).thenReturn(Optional.empty()); // Simuler l'absence de l'entité

        // Act & Assert
        assertThrows(RuntimeException.class, () -> myService.updateEntity(1, "New Name")); // Vérifier qu'une exception est levée
    }

    // Test de l'ID invalide lors de la recherche
    @Test
    public void testFindEntityById_InvalidId_ShouldThrowException() {
        // Vérifier qu'un ID négatif lève une exception
        assertThrows(IllegalArgumentException.class, () -> myService.findEntityById(-1));
    }

    // Test de l'exception levée par la base de données
    @Test
    public void testFindEntityById_DatabaseError_ShouldThrowException() {
        // Simuler une exception lors de l'accès à la base de données
        when(myRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // Vérifier que l'exception est bien propagée
        RuntimeException exception = assertThrows(RuntimeException.class, () -> myService.findEntityById(1));
        assertEquals("Database error", exception.getMessage());
    }
}
