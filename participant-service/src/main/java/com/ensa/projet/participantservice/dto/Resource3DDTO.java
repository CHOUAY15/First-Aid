package com.ensa.projet.participantservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource3DDTO {
    private Integer id;
    private String url;
    private String description;
    private String format;
}