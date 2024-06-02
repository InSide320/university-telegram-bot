package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.faculty.Faculty;
import com.example.universitytelegrambot.model.faculty.FacultyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FacultiesService {
    FacultyRepository facultyRepository;

    public FacultiesService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public void saveAndUpdateLoadFaculties(List<Faculty> facultiesFromFile) {
        List<String> existingFacultyNames = facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .toList();
        List<Faculty> newFaculties = facultiesFromFile.stream()
                .filter(faculty -> !existingFacultyNames.contains(faculty.getName()))
                .toList();
        if (!newFaculties.isEmpty()) {
            facultyRepository.saveAll(newFaculties);
        }
    }
}
