package com.ensa.projet.participantservice.controller;

import com.ensa.projet.participantservice.dto.KeycloakUserInfo;
import com.ensa.projet.participantservice.entities.Participant;
import com.ensa.projet.participantservice.entities.ParticipantAnswer;
import com.ensa.projet.participantservice.entities.TestResult;
import com.ensa.projet.participantservice.entities.TrainingProgress;
import com.ensa.projet.participantservice.service.interfaces.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService participantService;

    @Autowired
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }
    @PostMapping("/{participantId}/trainings/{trainingId}/join")
    public ResponseEntity<TrainingProgress> joinTraining(
            @PathVariable Integer participantId,
            @PathVariable Integer trainingId) {
        return ResponseEntity.ok(participantService.joinTraining(participantId, trainingId));
    }

    @PostMapping("/{participantId}/trainings/{trainingId}/submit-test")
    public ResponseEntity<TestResult> submitTest(
            @PathVariable Integer participantId,
            @PathVariable Integer trainingId,
            @RequestBody List<ParticipantAnswer> answers) {
        return ResponseEntity.ok(participantService.submitTest(participantId, trainingId, answers));
    }

    @PostMapping("/save/{userId}")
    public ResponseEntity<Participant> createParticipant(@PathVariable String userId,@RequestBody KeycloakUserInfo userInfo) {
        return ResponseEntity.ok(participantService.createParticipant(userId, userInfo));
    }

    @GetMapping("/get/{userId}")
    public  ResponseEntity<Participant> getParticipant(@PathVariable String userId) {
        return ResponseEntity.ok(participantService.getParticipantByUserId(userId));
    }
}
