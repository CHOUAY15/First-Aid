package com.ensa.projet.trainingservice.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private String iconPath;
    private String urlYtb;

    private String goals;
    @ElementCollection
    @CollectionTable(name = "training_instructions",
            joinColumns = @JoinColumn(name = "training_id"))
    @Column(name = "instruction", length = 500)
    @OrderColumn(name = "instruction_order")
    private List<String> instructions;


    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Quiz> quizzes=new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "training")
    private List<Course> courses=new ArrayList<>();




}
