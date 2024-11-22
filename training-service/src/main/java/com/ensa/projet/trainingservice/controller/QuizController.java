package com.ensa.projet.trainingservice.controller;

import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.entities.Quiz;
import com.ensa.projet.trainingservice.service.interfaces.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }
    @GetMapping("/{trainingId}")
    public ResponseEntity<List<QuizDTO>> getAllQuizByTrainingId(@PathVariable("trainingId") Integer trainingId) {
        return ResponseEntity.ok(quizService.getQuizzesByTrainingId(trainingId));
    }


}
