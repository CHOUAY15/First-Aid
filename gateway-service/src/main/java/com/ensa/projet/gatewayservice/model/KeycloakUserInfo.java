package com.ensa.projet.gatewayservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class KeycloakUserInfo {
    private String sub;  // Keycloak user ID
    private String email;
    private String firstName;
    private String lastName;
}