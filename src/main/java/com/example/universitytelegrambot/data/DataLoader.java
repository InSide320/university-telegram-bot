package com.example.universitytelegrambot.data;

import com.example.universitytelegrambot.model.documents.AdmissionDocuments;
import com.example.universitytelegrambot.model.faculty.*;
import com.example.universitytelegrambot.model.faculty.department.Department;
import com.example.universitytelegrambot.model.faculty.speciality.Specialty;
import com.example.universitytelegrambot.model.faculty.speciality.education.Coefficient;
import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Component
@Slf4j
public class DataLoader {

    private final ObjectMapper objectMapper;
    private final TypeFactory typeFactory;

    public DataLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.typeFactory = objectMapper.getTypeFactory();
    }

    private <T> List<T> loadFromFile(String filePath, Class<T> valueType) {
        CollectionType collectionType = typeFactory.constructCollectionType(List.class, valueType);
        try {
            byte[] fileContent = Files.readAllBytes(new File(filePath).toPath());
            String json = new String(fileContent, StandardCharsets.UTF_8);
            return objectMapper.readValue(json, collectionType);
        } catch (IOException e) {
            log.error("Error with reading file: {}", e.getMessage());
            return null;
        }
    }

    public List<Faculty> loadFacultiesFromFile(String filePath) {
        return loadFromFile(filePath, Faculty.class);
    }

    public List<EducationLevel> loadEducationLevelsFromFile(String filePath) {
        return loadFromFile(filePath, EducationLevel.class);
    }

    public List<Specialty> loadSpecialtiesFromFile(String filePath) {
        return loadFromFile(filePath, Specialty.class);
    }

    public List<AdmissionDocuments> loadAdmissionDocumentsFromFile(String filePath) {
        return loadFromFile(filePath, AdmissionDocuments.class);
    }

    public List<Department> loadDepartmentFromFile(String filePath) {
        return loadFromFile(filePath, Department.class);
    }

    public List<Coefficient> loadCoefficientsFromFile(String filePath) {
        return loadFromFile(filePath, Coefficient.class);
    }
}


