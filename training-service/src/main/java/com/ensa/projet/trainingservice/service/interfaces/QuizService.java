package com.ensa.projet.trainingservice.service.interfaces;

import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.entities.Quiz;

import java.util.List;

public interface QuizService {
    List<QuizDTO> getQuizzesByTrainingId(Integer trainingId);
    Quiz getQuiz(int id);
}
