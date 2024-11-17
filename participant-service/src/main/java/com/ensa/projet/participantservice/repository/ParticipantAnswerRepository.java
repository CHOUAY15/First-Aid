package com.ensa.projet.participantservice.repository;

import com.ensa.projet.participantservice.entities.ParticipantAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantAnswerRepository extends JpaRepository<ParticipantAnswer, Integer> {
}
