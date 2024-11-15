package com.ensa.projet.trainingservice.repository;

import com.ensa.projet.trainingservice.model.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository  extends JpaRepository<Quiz, Integer> {
    List<Quiz> findByTrainingId(int trainingId);

}
