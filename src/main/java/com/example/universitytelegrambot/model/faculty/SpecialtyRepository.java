package com.example.universitytelegrambot.model.faculty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    @Query("SELECT s from specialtiesTable s where s.educationLevel = :educationLevelId")
    List<Specialty> findByEducationLevel(@Param("educationLevelId") EducationLevel educationLevelId);

    @Query("SELECT s from specialtiesTable s where s.name=:name")
    Optional<Specialty> findByName(@Param("name") String name);

    @Query("SELECT s FROM specialtiesTable s WHERE s.name = :name")
    List<Specialty> findAllByName(@Param("name") String name);
}