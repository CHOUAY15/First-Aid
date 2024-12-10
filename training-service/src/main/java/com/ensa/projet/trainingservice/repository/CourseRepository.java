package com.ensa.projet.trainingservice.repository;

import com.ensa.projet.trainingservice.model.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
