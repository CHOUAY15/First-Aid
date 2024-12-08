package com.ensa.projet.participantservice.service.imple;

import com.ensa.projet.participantservice.client.TrainingServiceClient;
import com.ensa.projet.participantservice.dto.KeycloakUserInfo;
import com.ensa.projet.participantservice.dto.QuizDTO;
import com.ensa.projet.participantservice.dto.TrainingDTO;
import com.ensa.projet.participantservice.entities.*;
import com.ensa.projet.participantservice.exception.ResourceNotFoundException;
import com.ensa.projet.participantservice.repository.CertificationRepository;
import com.ensa.projet.participantservice.repository.ParticipantRepository;
import com.ensa.projet.participantservice.repository.TestResultRepository;
import com.ensa.projet.participantservice.repository.TrainingProgressRepository;
import com.ensa.projet.participantservice.service.interfaces.ParticipantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional


public class ParticipantSeriviceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final TrainingProgressRepository progressRepository;
    private final TestResultRepository testResultRepository;
    private final CertificationRepository certificationRepository;
    private final TrainingServiceClient trainingService;
    private static final float CERTIFICATION_SCORE_THRESHOLD = 70.0f;



    @Autowired
    public ParticipantSeriviceImpl(ParticipantRepository participantRepository, TrainingProgressRepository progressRepository, TestResultRepository testResultRepository, CertificationRepository certificationRepository, TrainingServiceClient trainingService) {
        this.participantRepository = participantRepository;
        this.progressRepository = progressRepository;
        this.testResultRepository = testResultRepository;
        this.certificationRepository = certificationRepository;
        this.trainingService = trainingService;
    }


    @Override
    @Transactional
    public Participant createParticipant(String userId, KeycloakUserInfo userInfo) {
        Optional<Participant> existingParticipant = participantRepository.findByUserId(userId);
        if (existingParticipant.isPresent()) {
            throw new ResourceNotFoundException("Participant already exists for user: " + userId);
        }
        Participant participant = Participant.builder()
                .userId(userId)
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .build();

        participant = participantRepository.save(participant);

        return participant;
    }

    @Override
    public Participant getParticipantByUserId(String userId) {
        return participantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found for user: " + userId));
    }


    @Override
    public TrainingProgress joinTraining(Integer participantId, Integer trainingId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new ResourceNotFoundException("Participant not found"));

        TrainingDTO trainingDTO = trainingService.getTraining(trainingId);

        TrainingProgress trainingProgress = TrainingProgress.builder()
                .trainingId(trainingId)
                .participant(participant)
                .progressPercentage(0.0f)
                .startDate(LocalDateTime.now())
                .status(ProgressStatus.IN_PROGRESS)
                .build();
        return progressRepository.save(trainingProgress);
    }

    @Override
    public TestResult submitTest(Integer participantId, Integer trainingId, List<ParticipantAnswer> answers) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(()->new ResourceNotFoundException("Participant not found"));
        List<QuizDTO> quizDTOS=trainingService.getQuizzes(trainingId);

        List<ParticipantAnswer> answerList=processAnswers(answers, quizDTOS);
        float score = calculateScore(answerList);
        boolean passed = score >= CERTIFICATION_SCORE_THRESHOLD;

        TestResult testResult = TestResult.builder()
                .participant(participant)
                .trainingId(trainingId)
                .userAnswers(answerList)
                .score(score)
                .submissionDate(LocalDateTime.now())
                .passed(passed)
                .build();
        TestResult savedResult = testResultRepository.save(testResult);

        if (passed) {
            generateCertification(participant, trainingId, score);
            updateTrainingProgress(participant, trainingId, ProgressStatus.CERTIFIED);
        }

        return savedResult;
    }



    private float calculateScore(List<ParticipantAnswer> userAnswers) {
        long correctAnswers = userAnswers.stream()
                .filter(ParticipantAnswer::isCorrect)
                .count();

        return (float) correctAnswers / userAnswers.size() * 100;
    }

    private List<ParticipantAnswer> processAnswers(List<ParticipantAnswer> answers, List<QuizDTO> quizDTOS) {
        return answers.stream()
                .map(answer -> {
                    QuizDTO quiz = findQuizById(quizDTOS, answer.getQuizId());
                    boolean isCorrect = quiz.getCorrectAnswerIndex().equals(answer.getSelectedAnswerIndex());

                    return ParticipantAnswer.builder()
                            .quizId(answer.getQuizId())
                            .selectedAnswerIndex(answer.getSelectedAnswerIndex())
                            .isCorrect(isCorrect)
                            .build();
                })
                .toList();
    }

    private void generateCertification(Participant participant, Integer trainingId, float score) {
        Certification certification = Certification.builder()
                .participant(participant)
                .trainingId(trainingId)
                .certificateNumber(generateCertificateNumber())
                .issueDate(LocalDateTime.now())
                .finalScore(score)
                .build();

        certificationRepository.save(certification);
    }

    private void updateTrainingProgress(Participant participant, Integer trainingId, ProgressStatus status) {
        TrainingProgress progress = progressRepository.findByParticipantIdAndTrainingId(participant.getId(), trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training progress not found"));

        progress.setStatus(status);
        progress.setProgressPercentage(100.0f);

        progressRepository.save(progress);
    }

    private String generateCertificateNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private QuizDTO findQuizById(List<QuizDTO> quizzes, Integer quizId) {
        return quizzes.stream()
                .filter(quiz -> quiz.getId().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }
}
