package com.ensa.projet.participantservice.dto;

import lombok.Data;

@Data
public class KeycloakUserInfo {
    private String sub;  // Keycloak user ID
    private String email;
    private String firstName;
    private String lastName;
}