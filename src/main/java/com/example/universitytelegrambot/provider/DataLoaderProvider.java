package com.example.universitytelegrambot.provider;

import com.example.universitytelegrambot.data.DataLoader;
import com.example.universitytelegrambot.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataLoaderProvider {
    private final DataLoader dataLoader;
    private final SpecialityService specialityService;
    private final FacultiesService facultiesService;
    private final DepartmentService departmentService;
    private final AdmissionService admissionService;
    private final EducationLevelService educationLevelService;
    private final CoefficientService coefficientService;

    public DataLoaderProvider(
            DataLoader dataLoader,
            SpecialityService specialityService,
            FacultiesService facultiesService,
            DepartmentService departmentService,
            AdmissionService admissionService,
            EducationLevelService educationLevelService, CoefficientService coefficientService) {
        this.dataLoader = dataLoader;
        this.specialityService = specialityService;
        this.facultiesService = facultiesService;
        this.departmentService = departmentService;
        this.admissionService = admissionService;
        this.educationLevelService = educationLevelService;
        this.coefficientService = coefficientService;
    }

    public void loadData() {
        facultiesService.saveAndUpdateLoadFaculties(dataLoader.loadFacultiesFromFile("db/faculties.json"));
        educationLevelService.loadAndUpdateLoadEducationLevel(dataLoader.loadEducationLevelsFromFile("db/education_levels.json"));
        coefficientService.setDataToDBFromFileDepartment(dataLoader.loadCoefficientsFromFile("db/coefficients.json"));
        specialityService.updateSpecialtiesAndSave(dataLoader.loadSpecialtiesFromFile("db/specialties.json"));
        admissionService.setDataToDBFromFileAdmissionDocuments(dataLoader.loadAdmissionDocumentsFromFile("db/documents.json"));
        departmentService.setDataToDBFromFileDepartment(dataLoader.loadDepartmentFromFile("db/department.json"));
    }
}
