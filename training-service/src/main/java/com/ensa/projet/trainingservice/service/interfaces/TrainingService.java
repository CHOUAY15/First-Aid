package com.ensa.projet.trainingservice.service.interfaces;

import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.dao.Resource3DDTO;
import com.ensa.projet.trainingservice.model.dao.TrainingDTO;

import java.util.List;

public interface TrainingService {

    public TrainingDTO createTraining(TrainingDTO trainingDTO);
    public List<TrainingDTO> getAllTrainings();
    public TrainingDTO getTrainingById(Integer id);
    public TrainingDTO updateTraining(Integer id, TrainingDTO trainingDTO);
    public void deleteTraining(Integer id);
    public Resource3DDTO addResource(Integer trainingId, Resource3DDTO resourceDTO);
    public QuizDTO addQuiz(Integer trainingId, QuizDTO quizDTO);





}
