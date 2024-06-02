package com.example.universitytelegrambot.model.faculty;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "educationLevelsTable")
@Table(name = "education_levels_table")
public class EducationLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Column(name = "study_form", nullable = false)
    private String studyForm;

    @ManyToOne
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @OneToMany(mappedBy = "educationLevel", cascade = CascadeType.ALL)
    private List<Specialty> specialties;
}