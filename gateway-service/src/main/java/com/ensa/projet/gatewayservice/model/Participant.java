package com.ensa.projet.gatewayservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class Participant {

    private Integer id;
    private String firstName;
    private String lastName;
    private String userId;

}
