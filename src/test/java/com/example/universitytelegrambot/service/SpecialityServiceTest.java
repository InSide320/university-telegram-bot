package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.faculty.EducationLevel;
import com.example.universitytelegrambot.model.faculty.EducationLevelRepository;
import com.example.universitytelegrambot.model.faculty.Specialty;
import com.example.universitytelegrambot.model.faculty.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SpecialityServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private EducationLevelRepository educationLevelRepository;

    @InjectMocks
    private SpecialityService specialityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetSpecialistsByEducationalLevel() {
        String educationalLevel = "Bachelor";
        String studyForm = "Full-time";
        EducationLevel educationLevelEntity = new EducationLevel();
        List<Specialty> expectedSpecialties = Collections.singletonList(new Specialty());

        when(educationLevelRepository.findByEducationalLevelAndStudyForm(educationalLevel, studyForm))
                .thenReturn(educationLevelEntity);
        when(specialtyRepository.findByEducationLevel(educationLevelEntity)).thenReturn(expectedSpecialties);

        List<Specialty> actualSpecialties = specialityService.getSpecialistsByEducationalLevel(educationalLevel, studyForm);

        assertEquals(expectedSpecialties, actualSpecialties);
        verify(educationLevelRepository, times(1)).findByEducationalLevelAndStudyForm(educationalLevel, studyForm);
        verify(specialtyRepository, times(1)).findByEducationLevel(educationLevelEntity);
    }
    @Test
    void testFindExistingSpecialtyByName() {
        Long id = 1L;
        Specialty specialty = new Specialty();
        specialty.setId(id);

        // Mock behavior of specialtyRepository
        when(specialtyRepository.findById(id)).thenReturn(Optional.of(specialty));

        // Invoke method under test
        Specialty foundSpecialty = specialityService.findExistingSpecialtyByName(id);

        // Assert
        assertEquals(specialty, foundSpecialty);
    }

    @Test
    void testUpdateSpecialtiesAndSave() {
        List<Specialty> specialtiesFromFile = new ArrayList<>();
        Specialty specialty = new Specialty();
        specialty.setId(1L);

        specialtiesFromFile.add(specialty);

        // Mock behavior of specialtyRepository
        when(specialtyRepository.findById(anyLong())).thenReturn(Optional.of(new Specialty()));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(new Specialty());

        // Invoke method under test
        specialityService.updateSpecialtiesAndSave(specialtiesFromFile);

        // Verify
        verify(specialtyRepository, times(specialtiesFromFile.size())).findById(anyLong());
        verify(specialtyRepository, times(specialtiesFromFile.size())).save(any(Specialty.class));
    }
}
