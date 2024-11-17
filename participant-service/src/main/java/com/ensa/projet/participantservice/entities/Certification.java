package com.ensa.projet.participantservice.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certifications")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

    private Integer trainingId;
    private String certificateNumber;
    private LocalDateTime issueDate;
    private float finalScore;
}