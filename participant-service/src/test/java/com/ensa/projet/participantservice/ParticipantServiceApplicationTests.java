package com.ensa.projet.participantservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")

@SpringBootTest
class ParticipantServiceApplicationTests {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

}
