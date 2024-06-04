package com.example.universitytelegrambot.model.faculty.department;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity(name = "compositionDepartmentTable")
@Table(name = "composition_department_table")
public class CompositionDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "position")
    private String position;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "url")
    private String url;

    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id", nullable = false)
    private Department department;
}
