package com.ensa.projet.participantservice.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participant_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ParticipantAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer quizId;
    private Integer selectedAnswerIndex;
    private boolean isCorrect;
}
