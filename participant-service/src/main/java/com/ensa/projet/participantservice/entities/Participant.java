package com.ensa.projet.participantservice.entities;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "participant")
//lombok
@Getter @Setter @ToString @Builder @NoArgsConstructor @AllArgsConstructor
public class Participant {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String userId;




}
