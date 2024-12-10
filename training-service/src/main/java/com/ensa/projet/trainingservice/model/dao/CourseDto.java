package com.ensa.projet.trainingservice.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CourseDto {

    private Long id;
    private String name;
    private String urlImage;
}
