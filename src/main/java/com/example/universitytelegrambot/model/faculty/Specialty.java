package com.example.universitytelegrambot.model.faculty;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "specialtiesTable")
@Table(name = "specialties_table")
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "educational_program")
    private String educationalProgram;

    @Column(name = "accreditation_date")
    private String accreditationDate;

    @ManyToOne
    @JoinColumn(name = "education_level_id", nullable = false)
    private EducationLevel educationLevel;
}