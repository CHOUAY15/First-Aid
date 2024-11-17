package com.ensa.projet.participantservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_progress")
//lombok
@Getter @Setter @ToString @Builder @NoArgsConstructor @AllArgsConstructor

public class TrainingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;
    private Integer trainingId;
    private float progressPercentage;
    private LocalDateTime startDate;
    @Enumerated(EnumType.STRING)
    private ProgressStatus status;


}
