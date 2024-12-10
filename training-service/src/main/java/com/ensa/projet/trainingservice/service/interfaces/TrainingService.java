package com.ensa.projet.trainingservice.service.interfaces;

import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.dao.TrainingDTO;

import java.util.List;

public interface TrainingService {

     TrainingDTO createTraining(TrainingDTO trainingDTO);
     List<TrainingDTO> getAllTrainings();
     TrainingDTO getTrainingById(Integer id);
     TrainingDTO updateTraining(Integer id, TrainingDTO trainingDTO);
     void deleteTraining(Integer id);

     QuizDTO addQuiz(Integer trainingId, QuizDTO quizDTO);





}
