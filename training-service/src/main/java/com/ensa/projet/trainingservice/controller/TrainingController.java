package com.ensa.projet.trainingservice.controller;


import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.dao.TrainingDTO;
import com.ensa.projet.trainingservice.service.interfaces.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trainings")
public class TrainingController {

    private TrainingService trainingService;
    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<TrainingDTO> createTraining(@RequestBody TrainingDTO trainingDTO) {
        TrainingDTO created = trainingService.createTraining(trainingDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<TrainingDTO>> getAllTrainings() {
        List<TrainingDTO> trainings = trainingService.getAllTrainings();
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingDTO> getTraining(@PathVariable Integer id) {
        TrainingDTO training = trainingService.getTrainingById(id);
        return ResponseEntity.ok(training);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingDTO> updateTraining(
            @PathVariable Integer id,
            @RequestBody TrainingDTO trainingDTO) {
        TrainingDTO updated = trainingService.updateTraining(id, trainingDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Integer id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/quizzes")
    public ResponseEntity<QuizDTO> addQuiz(
            @PathVariable Integer id,
            @RequestBody QuizDTO quizDTO) {
        QuizDTO added = trainingService.addQuiz(id, quizDTO);
        return new ResponseEntity<>(added, HttpStatus.CREATED);
    }
}
