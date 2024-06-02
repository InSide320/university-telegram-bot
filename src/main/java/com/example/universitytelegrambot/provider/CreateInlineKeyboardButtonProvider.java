package com.example.universitytelegrambot.provider;

import com.example.universitytelegrambot.constant.TelegramConstantVariable;
import com.example.universitytelegrambot.model.faculty.Department;
import com.example.universitytelegrambot.model.faculty.Specialty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CreateInlineKeyboardButtonProvider {
    private InlineKeyboardButton createInlineKeyboardButton(String text) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);

        // Truncate callbackData to 64 bytes
        String callbackData = text;
        byte[] bytes = callbackData.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 64) {
            callbackData = new String(bytes, 0, 64, StandardCharsets.UTF_8);
        }

        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }

    public List<List<InlineKeyboardButton>> sayEducationalLevel() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(createInlineKeyboardButton(TelegramConstantVariable.BACHELORS_LEVEL));
        rowsInline.add(rowInline);
        rowInline = new ArrayList<>();
        rowInline.add(createInlineKeyboardButton(TelegramConstantVariable.MASTERS_LEVEL));
        rowsInline.add(rowInline);
        return rowsInline;
    }

    public List<List<InlineKeyboardButton>> sayAdditionalInfoAboutDepartments(List<Department> departmentList) {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Department department : departmentList) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(department.getName());
            button.setCallbackData(TelegramConstantVariable.PREFIX_TO_CALLBACK_DEPARTMENT + department.getId());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        return rowsInline;
    }

    public List<List<InlineKeyboardButton>> confirmInformation() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Так");
        yesButton.setCallbackData(TelegramConstantVariable.YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("Ні");
        noButton.setCallbackData(TelegramConstantVariable.NO_BUTTON);

        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInline.add(rowInline);
        return rowsInline;
    }

    List<InlineKeyboardButton> getInlineKeyboardButtons(Specialty specialty) {
        String buttonText = specialty.getCode() + " " + specialty.getEducationalProgram();
        buttonText = buttonText.length() > 64 ? buttonText.substring(0, 64) : buttonText;

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData(TelegramConstantVariable.PREFIX_TO_CALLBACK_SPECIALTY + specialty.getId());

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(button);
        return rowInline;
    }

    public List<List<InlineKeyboardButton>> sayAdditionalInfoAboutSpecialty(List<Specialty> specialtyList) {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (Specialty specialty : specialtyList) {
            boolean isBachelorsLevelAndRemoveDuplicateInfo =
                    specialty.getEducationLevel().getName().equals(TelegramConstantVariable.BACHELORS_LEVEL)
                            &&
                            specialty.getStudyDurationMonths() == 4;
            boolean isMasterLevel = specialty.getEducationLevel().getName().equals(TelegramConstantVariable.MASTERS_LEVEL);
            if (isBachelorsLevelAndRemoveDuplicateInfo || isMasterLevel) {
                List<InlineKeyboardButton> rowInline = getInlineKeyboardButtons(specialty);
                rowsInline.add(rowInline);
            }
        }

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("<- Повернутись назад");
        button.setCallbackData(TelegramConstantVariable.BACK_TO_EDUCATION_LEVEL);

        rowInline.add(button);
        rowsInline.add(rowInline);

        return rowsInline;
    }
}
