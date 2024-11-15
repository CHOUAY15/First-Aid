package com.ensa.projet.trainingservice.repository;

import com.ensa.projet.trainingservice.model.entities.Ressource3D;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RessourceRepository extends JpaRepository<Ressource3D,Integer> {
    List<Ressource3D> findByTrainingId(Integer id);

}
