package com.example.universitytelegrambot.model.faculty;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "facultiesTable")
@Table(name = "faculties_table")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    private List<EducationLevel> educationLevels;
}
