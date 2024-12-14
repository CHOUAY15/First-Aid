package com.ensa.projet.trainingservice.repository;

import com.ensa.projet.trainingservice.model.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;



public interface TrainingRepository  extends JpaRepository<Training, Integer> {

}
