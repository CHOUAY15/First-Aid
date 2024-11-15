package com.ensa.projet.trainingservice.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuizDTO {
    private Integer id;
    private String question;
    private List<String> options;
    private Integer correctAnswerIndex;
}
