package com.example.universitytelegrambot.model.faculty.speciality.education;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "coefficientsTable")
@Table(name = "coefficients_table")
public class Coefficient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "first_main_subject")
    private Double firstMainSubject;

    @Column(name = "second_main_subject")
    private Double secondMainSubject;

    @Column(name = "third_main_subject")
    private Double thirdMainSubject;

    @Column(name = "foreign_language")
    private Double foreignLanguage;

    @Column(name = "biology")
    private Double biology;

    @Column(name = "physics")
    private Double physics;

    @Column(name = "chemistry")
    private Double chemistry;
}