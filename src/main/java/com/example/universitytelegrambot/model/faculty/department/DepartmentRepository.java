package com.example.universitytelegrambot.model.faculty.department;

import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT s FROM departmentTable s where s.name = :name")
    EducationLevel findByName(@Param("name") String name);
}
