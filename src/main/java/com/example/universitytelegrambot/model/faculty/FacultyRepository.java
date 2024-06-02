package com.example.universitytelegrambot.model.faculty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    @Query("SELECT distinct f FROM facultiesTable f LEFT JOIN FETCH f.educationLevels")
    List<Faculty> findAllWithEducationLevels();
}
