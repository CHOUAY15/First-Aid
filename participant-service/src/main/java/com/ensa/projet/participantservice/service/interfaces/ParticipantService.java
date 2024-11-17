package com.ensa.projet.participantservice.service.interfaces;

import com.ensa.projet.participantservice.entities.ParticipantAnswer;
import com.ensa.projet.participantservice.entities.TestResult;
import com.ensa.projet.participantservice.entities.TrainingProgress;

import java.util.List;

public interface ParticipantService {
    TrainingProgress joinTraining(Integer participantId, Integer trainingId);
    TestResult submitTest(Integer participantId, Integer trainingId, List<ParticipantAnswer> answers);
}
