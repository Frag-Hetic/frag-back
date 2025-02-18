package com.projet.hetic.frag.service;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    // Champ de version pour la concurrence optimiste
    @Version
    private int version;

    // Constructeur par défaut nécessaire pour JPA
    public MyEntity() {}

    // Constructeur avec deux arguments pour l'id et le nom
    public MyEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
