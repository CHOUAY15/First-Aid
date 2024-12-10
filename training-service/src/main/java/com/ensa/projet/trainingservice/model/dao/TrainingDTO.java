package com.ensa.projet.trainingservice.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {
    private Integer id;
    private String iconPath;
    private String title;
    private String goals;
    private String urlYtb;
    private List<String> instructions;
    private List<CourseDto> courses;
    private List<QuizDTO> quizzes;

}