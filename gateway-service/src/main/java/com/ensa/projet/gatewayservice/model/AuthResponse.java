package com.ensa.projet.gatewayservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Integer participantId;
    private String userId;
}