package com.ensa.projet.participantservice.repository;

import com.ensa.projet.participantservice.entities.TrainingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingProgressRepository extends JpaRepository<TrainingProgress, Integer> {
   Optional<TrainingProgress>  findByParticipantIdAndTrainingId(Integer participantId, Integer trainingId);
}
