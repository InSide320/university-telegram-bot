package com.example.universitytelegrambot.model.faculty.speciality;

import com.example.universitytelegrambot.model.faculty.speciality.education.Coefficient;
import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "tuition_fee")
    private Double tuitionFee;

    @Column(name = "study_duration_months")
    private Double studyDurationMonths;

    @ManyToOne
    @JoinColumn(name = "education_level_id", nullable = false)
    private EducationLevel educationLevel;

    @ManyToOne
    @JoinColumn(name = "coefficient_id", referencedColumnName = "id")
    private Coefficient coefficients;
}