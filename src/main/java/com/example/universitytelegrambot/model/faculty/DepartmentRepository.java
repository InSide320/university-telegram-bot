package com.example.universitytelegrambot.model.faculty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT s FROM departmentTable s where s.name = :name")
    EducationLevel findByName(@Param("name") String name);
}
