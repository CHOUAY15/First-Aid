package com.ensa.projet.trainingservice.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Training")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    @ElementCollection
    @CollectionTable(name = "training_instructions",
            joinColumns = @JoinColumn(name = "training_id"))
    @Column(name = "instruction", length = 500)
    @OrderColumn(name = "instruction_order")
    private List<String> instructions;
    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes;
    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ressource3D> ressources;



}
