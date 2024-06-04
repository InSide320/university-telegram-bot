package com.example.universitytelegrambot.provider;

import com.example.universitytelegrambot.constant.TelegramConstantVariable;
import com.example.universitytelegrambot.model.faculty.department.Department;
import com.example.universitytelegrambot.model.faculty.Faculty;
import com.example.universitytelegrambot.model.faculty.speciality.Specialty;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateInlineKeyboardButtonProviderTest {

    @Test
    void testGetInlineKeyboardButtons() {
        CreateInlineKeyboardButtonProvider provider = new CreateInlineKeyboardButtonProvider();

        // Create a sample Specialty object
        Specialty specialty = new Specialty();
        specialty.setCode("TEST_CODE");
        specialty.setEducationalProgram("Test Educational Program");
        // Set other properties if needed

        // Invoke the method under test
        List<InlineKeyboardButton> buttons = provider.getInlineKeyboardButtons(specialty);

        // Assertions
        assertEquals(1, buttons.size());
        InlineKeyboardButton button = buttons.get(0);
        assertEquals("TEST_CODE Test Educational Program", button.getText());
        // Add more assertions if needed
    }

    @Test
    void testSayEducationalLevel() {
        CreateInlineKeyboardButtonProvider provider = new CreateInlineKeyboardButtonProvider();

        // Test sayEducationalLevel method
        List<List<InlineKeyboardButton>> rowsInline = provider.sayEducationalLevel();

        assertEquals(2, rowsInline.size());
        assertEquals(1, rowsInline.get(0).size());
        assertEquals(TelegramConstantVariable.BACHELORS_LEVEL, rowsInline.get(0).get(0).getText());
        assertEquals(1, rowsInline.get(1).size());
        assertEquals(TelegramConstantVariable.MASTERS_LEVEL, rowsInline.get(1).get(0).getText());
    }

    @Test
    void testSayAdditionalInfoAboutDepartments() {
        CreateInlineKeyboardButtonProvider provider = new CreateInlineKeyboardButtonProvider();

        // Create sample department data
        List<Department> departmentList = new ArrayList<>();
        departmentList.add(new Department(1L, "Department 1", "link", "desck", new Faculty()));
        departmentList.add(new Department(2L, "Department 2", "link", "desck", new Faculty()));

        // Invoke the method under test
        List<List<InlineKeyboardButton>> result = provider.sayAdditionalInfoAboutDepartments(departmentList);

        // Assertions
        assertEquals(2, result.size());

        // First row
        assertEquals(1, result.get(0).size());
        assertEquals("Department 1", result.get(0).get(0).getText());
        assertEquals(TelegramConstantVariable.PREFIX_TO_CALLBACK_DEPARTMENT + "1", result.get(0).get(0).getCallbackData());

        // Second row
        assertEquals(1, result.get(1).size());
        assertEquals("Department 2", result.get(1).get(0).getText());
        assertEquals(TelegramConstantVariable.PREFIX_TO_CALLBACK_DEPARTMENT + "2", result.get(1).get(0).getCallbackData());
    }
}
