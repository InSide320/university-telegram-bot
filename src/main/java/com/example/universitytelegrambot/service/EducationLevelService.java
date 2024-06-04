package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevel;
import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EducationLevelService {
    EducationLevelRepository educationLevelRepository;

    public EducationLevelService(EducationLevelRepository educationLevelRepository) {
        this.educationLevelRepository = educationLevelRepository;
    }

    public void loadAndUpdateLoadEducationLevel(List<EducationLevel> educationLevelsFromFile) {
        List<EducationLevel> existingEducationLevels = educationLevelRepository.findAll();
        List<EducationLevel> newEducationLevels = educationLevelsFromFile.stream()
                .filter(level -> existingEducationLevels.stream().noneMatch(existingLevel -> existingLevel.getName().equals(level.getName())))
                .toList();
        if (!newEducationLevels.isEmpty()) {
            educationLevelRepository.saveAll(newEducationLevels);
        }
    }
}
