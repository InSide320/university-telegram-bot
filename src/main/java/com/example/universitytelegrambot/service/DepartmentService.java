package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.faculty.department.Department;
import com.example.universitytelegrambot.model.faculty.department.DepartmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DepartmentService {
    DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public void setDataToDBFromFileDepartment(List<Department> departmentsFromFile) {
        List<String> existingDepartmentNames = departmentRepository.findAll().stream()
                .map(Department::getName)
                .toList();
        List<Department> newDepartments = departmentsFromFile.stream()
                .filter(department -> !existingDepartmentNames.contains(department.getName()))
                .toList();
        if (!newDepartments.isEmpty()) {
            departmentRepository.saveAll(newDepartments);
        }
    }
}
