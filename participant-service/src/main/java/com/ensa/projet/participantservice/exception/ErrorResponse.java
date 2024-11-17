package com.ensa.projet.participantservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//lombok dev tools
@Data @AllArgsConstructor @NoArgsConstructor @Builder

public class ErrorResponse {
    private String error;
    private String message;


}