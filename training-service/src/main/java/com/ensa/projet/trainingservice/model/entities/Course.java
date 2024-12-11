package com.ensa.projet.trainingservice.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@Data @AllArgsConstructor
@Builder
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;
}
