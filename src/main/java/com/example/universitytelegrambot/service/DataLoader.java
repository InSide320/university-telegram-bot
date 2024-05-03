package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.faculty.EducationLevel;
import com.example.universitytelegrambot.model.faculty.Faculty;
import com.example.universitytelegrambot.model.faculty.Specialty;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
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
            return objectMapper.readValue(new File(filePath), collectionType);
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
}
