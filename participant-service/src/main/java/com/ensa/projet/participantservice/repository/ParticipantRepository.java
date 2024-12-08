package com.ensa.projet.participantservice.repository;

import com.ensa.projet.participantservice.entities.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    Optional<Participant> findByUserId(String userId);
}
