package com.ensa.projet.trainingservice.repository;

import com.ensa.projet.trainingservice.model.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRepository  extends JpaRepository<Training, Integer> {
    List<Training> findByTitleContainingIgnoreCase(String title);
}
