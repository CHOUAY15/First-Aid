package com.ensa.projet.participantservice.service.interfaces;

import com.ensa.projet.participantservice.dto.KeycloakUserInfo;
import com.ensa.projet.participantservice.entities.Participant;
import com.ensa.projet.participantservice.entities.ParticipantAnswer;
import com.ensa.projet.participantservice.entities.TestResult;
import com.ensa.projet.participantservice.entities.TrainingProgress;

import java.util.List;

public interface ParticipantService {
    Participant createParticipant(String userId, KeycloakUserInfo userInfo);
    TrainingProgress joinTraining(Integer participantId, Integer trainingId);
    TestResult submitTest(Integer participantId, Integer trainingId, List<ParticipantAnswer> answers);
    Participant getParticipantByUserId(String userId);
}
