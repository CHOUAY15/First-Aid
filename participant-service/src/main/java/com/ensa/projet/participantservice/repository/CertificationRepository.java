package com.ensa.projet.participantservice.repository;

import com.ensa.projet.participantservice.entities.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {

}
