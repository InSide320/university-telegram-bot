package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.documents.AdmissionDocuments;
import com.example.universitytelegrambot.model.documents.AdmissionDocumentsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AdmissionService {
    AdmissionDocumentsRepository admissionDocumentsRepository;

    public AdmissionService(AdmissionDocumentsRepository admissionDocumentsRepository) {
        this.admissionDocumentsRepository = admissionDocumentsRepository;
    }

    public void setDataToDBFromFileAdmissionDocuments(List<AdmissionDocuments> documentsFromFile) {
        List<String> existingDocumentCopies = admissionDocumentsRepository.findAll().stream()
                .map(AdmissionDocuments::getIdentificationDocumentCopy)
                .toList();
        List<AdmissionDocuments> newDocuments = documentsFromFile.stream()
                .filter(doc -> !existingDocumentCopies.contains(doc.getIdentificationDocumentCopy()))
                .toList();
        if (!newDocuments.isEmpty()) {
            admissionDocumentsRepository.saveAll(newDocuments);
        }
    }
}
