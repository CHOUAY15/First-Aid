package com.ensa.projet.trainingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")

@SpringBootTest
class TrainingServiceApplicationTests {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

}
