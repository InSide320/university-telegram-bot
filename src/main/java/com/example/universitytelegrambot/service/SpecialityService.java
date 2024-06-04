package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.constant.TelegramConstantVariable;
import com.example.universitytelegrambot.model.faculty.speciality.Specialty;
import com.example.universitytelegrambot.model.faculty.speciality.SpecialtyRepository;
import com.example.universitytelegrambot.model.faculty.speciality.education.CoefficientRepository;
import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevel;
import com.example.universitytelegrambot.model.faculty.speciality.education.level.EducationLevelRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SpecialityService {

    private final SpecialtyRepository specialtyRepository;
    private final EducationLevelRepository educationLevelRepository;
    private final CoefficientRepository coefficientRepository;


    public SpecialityService(SpecialtyRepository specialtyRepository,
                             EducationLevelRepository educationLevelRepository, CoefficientRepository coefficientRepository) {
        this.specialtyRepository = specialtyRepository;
        this.educationLevelRepository = educationLevelRepository;
        this.coefficientRepository = coefficientRepository;
    }

    public List<Specialty> getSpecialistsByEducationalLevel(String educationalLevel, String studyForm) {
        EducationLevel educationLevel =
                educationLevelRepository.findByEducationalLevelAndStudyForm(educationalLevel, studyForm);
        return specialtyRepository.findByEducationLevel(educationLevel);
    }

    public String sendSpecialityMessage(Specialty specialty) {

        String emojiInformationSource = EmojiParser.parseToUnicode(":information_source:");

        StringBuilder message = new StringBuilder(EmojiParser.parseToUnicode(
                ":mortar_board: <b>Спеціальність: </b>" + specialty.getCode() + " " + specialty.getName() + "\n" +
                        ":mortar_board:" + "<b>Освітня програма: </b>" + specialty.getEducationalProgram() + "\n\n"));

        if (specialty.getEducationLevel().getName().equals(TelegramConstantVariable.BACHELORS_LEVEL)) {
            message.append("<b>").append(specialty.getCoefficients().getName()).append(":</b>")
                    .append("\n")
                    .append(emojiInformationSource)
                    .append(" К1 - Українська мова: ").append(specialty.getCoefficients().getFirstMainSubject())
                    .append("\n")
                    .append(emojiInformationSource)
                    .append("К2 - Математика: ").append(specialty.getCoefficients().getSecondMainSubject())
                    .append("\n")
                    .append(emojiInformationSource)
                    .append(" К3 - Історія України: ").append(specialty.getCoefficients().getThirdMainSubject())
                    .append("\n")
                    .append(emojiInformationSource)
                    .append(" К3 - Іноземна мова: ").append(specialty.getCoefficients().getForeignLanguage())
                    .append("\n")
                    .append(emojiInformationSource)
                    .append(" К3 - Біологія: ").append(specialty.getCoefficients().getBiology())
                    .append("\n")
                    .append(emojiInformationSource)
                    .append(" К3 - Фізика: ").append(specialty.getCoefficients().getPhysics())
                    .append("\n")
                    .append(emojiInformationSource)
                    .append(" К3 - Хімія: ").append(specialty.getCoefficients().getChemistry())
                    .append("\n\n");
        } else {
            message.append("<b>").append(specialty.getCoefficients().getName()).append(":</b>").append("\n")
                    .append(emojiInformationSource)
                    .append("K1 - Іноземна мова: ").append(specialty.getCoefficients().getFirstMainSubject()).append("\n")
                    .append(emojiInformationSource)
                    .append("K2 - ТЗНК: ").append(specialty.getCoefficients().getSecondMainSubject()).append("\n")
                    .append(emojiInformationSource)
                    .append("K3 - Іспит: ").append(specialty.getCoefficients().getThirdMainSubject()).append("\n\n");
        }

        List<Specialty> specialities = specialtyRepository.findAllByName(specialty.getName());

        for (Specialty specialty1 : specialities) {
            boolean isIdentityEducationLevel =
                    specialty1.getEducationLevel().getName()
                            .equals(
                                    specialty.getEducationLevel().getName()
                            );

            if (isIdentityEducationLevel) {
                message.append(emojiInformationSource).append("<b>Вартість за семестр на ")
                        .append(specialty1.getEducationLevel().getStudyForm().toUpperCase())
                        .append(" форма навчання: </b>")
                        .append(String.format("%.2f", specialty1.getTuitionFee())).append(" грн.")
                        .append("\n");

                message.append("<b>Тривалість навчання: </b>");

                if (specialty1.getStudyDurationMonths() == 4) {
                    message.append(String.format("%.0f", specialty1.getStudyDurationMonths()))
                            .append(" - семестри, навчання 3 роки та 10 місяців").append("\n");
                } else if (specialty1.getStudyDurationMonths() == 1.5) {
                    message.append(specialty1.getStudyDurationMonths())
                            .append(" - семестра, навчання 1 рік та 5 місяців").append("\n");
                } else if (specialty1.getStudyDurationMonths() == 2) {
                    message.append(String.format("%.0f", specialty1.getStudyDurationMonths()))
                            .append(" - семестри, навчання 1 рік та 10 місяців").append("\n");
                } else if (specialty1.getStudyDurationMonths() == 3) {
                    message.append(String.format("%.0f", specialty1.getStudyDurationMonths()))
                            .append(" - семестри, навчання 2 роки та 10 місяців").append("\n");
                } else {
                    message.append("<b>Тривалість навчання: </b>")
                            .append("Не зазначина. Потрібно уточнювати на порталі університету чи зв'язатись з примальною комісєю.")
                            .append("\n");
                }
                message.append("<b>Загальна вартість навчання: </b>")
                        .append(String.format("%.2f", specialty1.getTuitionFee() * specialty1.getStudyDurationMonths())).append(" грн.")
                        .append("\n\n");
            }
        }
        message.append(EmojiParser.parseToUnicode(":calendar:" + "<b>Акредетація до: </b>"))
                .append(specialty.getAccreditationDate()).append("\n");

        return message.toString();
    }

    public Specialty findExistingSpecialtyByName(Long id) {
        Optional<Specialty> existingSpecialtyOptional = specialtyRepository.findById(id);
        return existingSpecialtyOptional.orElse(null);
    }

    public void updateSpecialtiesAndSave(List<Specialty> specialtiesFromFile) {
        for (Specialty specialty : specialtiesFromFile) {
            Specialty existingSpecialty = this.findExistingSpecialtyByName(specialty.getId());
            if (existingSpecialty != null) {
                existingSpecialty.setCode(specialty.getCode());
                existingSpecialty.setEducationalProgram(specialty.getEducationalProgram());
                existingSpecialty.setAccreditationDate(specialty.getAccreditationDate());
                existingSpecialty.setTuitionFee(specialty.getTuitionFee());
                existingSpecialty.setStudyDurationMonths(specialty.getStudyDurationMonths());
                specialtyRepository.save(existingSpecialty);
            } else {
                // Додавання нової спеціальності
                specialtyRepository.save(specialty);
            }
        }
    }
}
