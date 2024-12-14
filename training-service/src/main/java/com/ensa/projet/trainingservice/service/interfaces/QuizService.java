package com.ensa.projet.trainingservice.service.interfaces;

import com.ensa.projet.trainingservice.model.dao.QuizDTO;


import java.util.List;

public interface QuizService {
    List<QuizDTO> getQuizzesByTrainingId(Integer trainingId);
}
