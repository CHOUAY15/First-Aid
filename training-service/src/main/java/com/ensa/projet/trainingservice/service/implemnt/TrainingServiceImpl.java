package com.ensa.projet.trainingservice.service.implemnt;

import com.ensa.projet.trainingservice.exception.ResourceNotFoundException;
import com.ensa.projet.trainingservice.model.dao.CourseDto;
import com.ensa.projet.trainingservice.model.dao.QuizDTO;
import com.ensa.projet.trainingservice.model.dao.TrainingDTO;
import com.ensa.projet.trainingservice.model.entities.Course;
import com.ensa.projet.trainingservice.model.entities.Quiz;
import com.ensa.projet.trainingservice.model.entities.Training;
import com.ensa.projet.trainingservice.repository.QuizRepository;
import com.ensa.projet.trainingservice.repository.TrainingRepository;
import com.ensa.projet.trainingservice.service.interfaces.TrainingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Transactional


public class TrainingServiceImpl  implements TrainingService {


    private final TrainingRepository trainingRepository;
    private final QuizRepository quizRepository;


    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository,

                           QuizRepository quizRepository) {
        this.trainingRepository = trainingRepository;

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
                .toList();
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
                .iconPath(trainingDTO.getIconPath())
                .urlYtb(trainingDTO.getUrlYtb())
                .goals(trainingDTO.getGoals())
                .instructions(trainingDTO.getInstructions())
                .build();
    }
    private TrainingDTO convertToDto(Training training) {
        return TrainingDTO.builder()
                .id(training.getId())
                .title(training.getTitle())
                .iconPath(training.getIconPath())
                .goals(training.getGoals())
                .instructions(training.getInstructions())
                .urlYtb(training.getUrlYtb())
                .courses(training.getCourses().stream()
                        .map(this::convertToCourseDTO)
                        .toList())
                .quizzes(training.getQuizzes().stream()
                        .map(this::convertToQuizDTO)
                        .toList())
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
    private CourseDto convertToCourseDTO(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .description(course.getDescription())
                .name(course.getName())
                .urlImage(course.getUrlImage())
                .build();
    }

    private void updateTrainingFromDTO(Training entity, TrainingDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setGoals(dto.getGoals());
        entity.setInstructions(dto.getInstructions());
    }
}
