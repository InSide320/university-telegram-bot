package com.example.universitytelegrambot.provider;

import com.example.universitytelegrambot.data.DataLoader;
import com.example.universitytelegrambot.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class DataLoaderProviderTest {

    @Mock
    private DataLoader dataLoader;

    @Mock
    private SpecialityService specialityService;

    @Mock
    private FacultiesService facultiesService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private AdmissionService admissionService;

    @Mock
    private EducationLevelService educationLevelService;

    @InjectMocks
    private DataLoaderProvider dataLoaderProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLoadData() {
        dataLoaderProvider.loadData();

        verify(facultiesService).saveAndUpdateLoadFaculties(dataLoader.loadFacultiesFromFile("db/faculties.json"));
        verify(educationLevelService).loadAndUpdateLoadEducationLevel(dataLoader.loadEducationLevelsFromFile("db/education_levels.json"));
        verify(specialityService).updateSpecialtiesAndSave(dataLoader.loadSpecialtiesFromFile("db/specialties.json"));
        verify(admissionService).setDataToDBFromFileAdmissionDocuments(dataLoader.loadAdmissionDocumentsFromFile("db/documents.json"));
        verify(departmentService).setDataToDBFromFileDepartment(dataLoader.loadDepartmentFromFile("db/department.json"));
    }
}
