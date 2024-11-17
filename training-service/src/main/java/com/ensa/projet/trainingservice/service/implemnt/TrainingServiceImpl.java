package com.ensa.projet.trainingservice.service.implemnt;

import com.ensa.projet.trainingservice.exception.ResourceNotFoundException;
import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.dao.Resource3DDTO;
import com.ensa.projet.trainingservice.model.dao.TrainingDTO;
import com.ensa.projet.trainingservice.model.entities.Quiz;
import com.ensa.projet.trainingservice.model.entities.Ressource3D;
import com.ensa.projet.trainingservice.model.entities.Training;
import com.ensa.projet.trainingservice.repository.QuizRepository;
import com.ensa.projet.trainingservice.repository.RessourceRepository;
import com.ensa.projet.trainingservice.repository.TrainingRepository;
import com.ensa.projet.trainingservice.service.interfaces.TrainingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional


public class TrainingServiceImpl  implements TrainingService {


    private final TrainingRepository trainingRepository;
    private final QuizRepository quizRepository;
    private final RessourceRepository ressourceRepository;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository,
                           RessourceRepository resourceRepository,
                           QuizRepository quizRepository) {
        this.trainingRepository = trainingRepository;
        this.ressourceRepository = resourceRepository;
        this.quizRepository = quizRepository;
    }

    @Override
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {
        Training training = convertToEntity(trainingDTO);
        Training savedTraining =trainingRepository.save(training);
        return convertToDto(savedTraining);
    }

    @Override
    public List<TrainingDTO> getAllTrainings() {
        return trainingRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TrainingDTO getTrainingById(Integer id) {
        Optional<Training> training = trainingRepository.findById(id);
        return training.map(this::convertToDto).orElse(null);
    }

    @Override
    public TrainingDTO updateTraining(Integer id, TrainingDTO trainingDTO) {
        Training existingTraining = trainingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));

        updateTrainingFromDTO(existingTraining, trainingDTO);
        Training updatedTraining = trainingRepository.save(existingTraining);
        return convertToDto(updatedTraining);
    }

    @Override
    public void deleteTraining(Integer id) {
        if (!trainingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Training not found with id: " + id);
        }
        trainingRepository.deleteById(id);
    }

    @Override
    public Resource3DDTO addResource(Integer trainingId, Resource3DDTO resourceDTO) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found"));

        Ressource3D resource = Ressource3D.builder()
                .training(training)
                .url(resourceDTO.getUrl())
                .description(resourceDTO.getDescription())
                .format(resourceDTO.getFormat())
                .build();

        Ressource3D savedResource = ressourceRepository.save(resource);
        return convertToResourceDTO(savedResource);
    }

    @Override
    public QuizDTO addQuiz(Integer trainingId, QuizDTO quizDTO) {
        Training training =trainingRepository.findById(trainingId)
                .orElseThrow(()->new ResourceNotFoundException("training not found"));
        Quiz quiz = Quiz.builder()
                .training(training)
                .question(quizDTO.getQuestion())
                .options(quizDTO.getOptions())
                .correctAnswerIndex(quizDTO.getCorrectAnswerIndex())
                .build();

        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToQuizDTO(savedQuiz);
    }

    private Training convertToEntity(TrainingDTO trainingDTO) {
        return Training.builder()
                .id(trainingDTO.getId())
                .title(trainingDTO.getTitle())
                .description(trainingDTO.getDescription())
                .instructions(trainingDTO.getInstructions())
                .build();
    }
    private TrainingDTO convertToDto(Training training) {
        return TrainingDTO.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .instructions(training.getInstructions())
                .resource(convertToResourceDTO(training.getRessource()))
                .quizzes(training.getQuizzes().stream()
                        .map(this::convertToQuizDTO)
                        .collect(Collectors.toList()))
                .build();
    }
    private Resource3DDTO convertToResourceDTO(Ressource3D resource) {
        return Resource3DDTO.builder()
                .id(resource.getId())
                .url(resource.getUrl())
                .description(resource.getDescription())
                .format(resource.getFormat())
                .build();
    }

    private QuizDTO convertToQuizDTO(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .question(quiz.getQuestion())
                .options(quiz.getOptions())
                .correctAnswerIndex(quiz.getCorrectAnswerIndex())
                .build();
    }

    private void updateTrainingFromDTO(Training entity, TrainingDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setInstructions(dto.getInstructions());
    }
}
