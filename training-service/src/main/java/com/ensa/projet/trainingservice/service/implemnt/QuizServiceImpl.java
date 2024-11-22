package com.ensa.projet.trainingservice.service.implemnt;

import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.entities.Quiz;

import com.ensa.projet.trainingservice.repository.QuizRepository;

import com.ensa.projet.trainingservice.service.interfaces.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;

    }

    @Override
    public List<QuizDTO> getQuizzesByTrainingId(Integer trainingId) {
        List<Quiz> quizzes = quizRepository.findByTrainingId(trainingId);

        return quizzes.stream().map(
                this::convertToQuizDTO
        ).toList();
    }

    @Override
    public Quiz getQuiz(int id) {
        return null;
    }

    private QuizDTO convertToQuizDTO(Quiz quiz) {

        return QuizDTO.builder()
                .id(quiz.getId())
                .question(quiz.getQuestion())
                .correctAnswerIndex(quiz.getCorrectAnswerIndex())
                .options(quiz.getOptions())
                .build();


    }
}
