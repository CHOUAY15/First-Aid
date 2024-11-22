package com.ensa.projet.participantservice.client;

import com.ensa.projet.participantservice.dto.QuizDTO;
import com.ensa.projet.participantservice.dto.TrainingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="training-service")
public interface TrainingServiceClient {


    @GetMapping("/trainings/{id}")
    TrainingDTO getTraining(@PathVariable Integer id);

    @GetMapping("/quiz/{trainingId}")
    List<QuizDTO> getQuizzes(@PathVariable Integer trainingId);


}
