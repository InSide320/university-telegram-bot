package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.faculty.speciality.education.Coefficient;
import com.example.universitytelegrambot.model.faculty.speciality.education.CoefficientRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CoefficientService {
    private final CoefficientRepository coefficientRepository;

    public void setDataToDBFromFileDepartment(List<Coefficient> coefficientList) {
        List<String> existingDepartmentNames = coefficientRepository.findAll().stream()
                .map(Coefficient::getName)
                .toList();
        List<Coefficient> newCoefficients = coefficientList.stream()
                .filter(coefficient -> !existingDepartmentNames.contains(coefficient.getName()))
                .toList();
        if (!newCoefficients.isEmpty()) {
            coefficientRepository.saveAll(newCoefficients);
        }
    }
}
