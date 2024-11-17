package com.ensa.projet.participantservice.repository;

import com.ensa.projet.participantservice.entities.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
}
