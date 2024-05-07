package com.example.universitytelegrambot.model.documents;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "admission_documents")
public class AdmissionDocuments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificationDocumentCopy;

    private String taxpayerIdentificationCopy;

    @Column(name = "special_conditions_documents_copies")
    private String specialConditionsDocumentsCopies;

    @Column(name = "military_registration_document_copy")
    private String militaryRegistrationDocumentCopy;

    @Column(name = "previous_education_document_copy")
    private String previousEducationDocumentCopy;

    @Column(name = "external_evaluation_certificate")
    private String externalEvaluationCertificate;

    @Column(name = "ukrainian_language_zno_results")
    private String ukrainianLanguageZNOResults;

    private String photographs;

    @Column(name = "folder_with_files")
    private String folderWithFiles;

    @Column(name = "envelopes_and_files")
    private String envelopesAndFiles;
}
