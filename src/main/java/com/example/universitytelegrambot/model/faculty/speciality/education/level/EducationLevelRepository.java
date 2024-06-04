package com.example.universitytelegrambot.model.faculty.speciality.education.level;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EducationLevelRepository extends JpaRepository<EducationLevel, Long> {
    @Query("SELECT e FROM educationLevelsTable e LEFT JOIN FETCH e.specialties")
    List<EducationLevel>
    findAllWithSpecialties();

    @Query("SELECT s FROM educationLevelsTable s where s.name = :educationLevel and s.studyForm=:studyForm")
    EducationLevel findByEducationalLevelAndStudyForm(@Param("educationLevel") String educationLevel, @Param("studyForm") String studyForm);
}
