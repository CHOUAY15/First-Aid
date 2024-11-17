package com.ensa.projet.participantservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Integer id;
    private String question;
    private List<String> options;
    private Integer correctAnswerIndex;
}
