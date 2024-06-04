package com.example.universitytelegrambot.provider;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardMarkupProvider {

    public ReplyKeyboardMarkup createPublicKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Документи для вступу");
        row1.add("Спеціальності фактультету");
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Про факультет");
        row2.add("Перелік кафедр");
        row2.add("Допомога");
        keyboardRows.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Реєстрація користувача");
        row3.add("Перевірка персональних даних");
        row3.add("Видалити персональні дані");
        keyboardRows.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup createOwnerKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add("Редагувати спеціальності");
        keyboardButtons.add("Редагувати кафедри");
        keyboardButtons.add("Редагувати освітні рівні");
        keyboardRows.add(keyboardButtons);

        keyboardButtons = new KeyboardRow();
        keyboardButtons.add("Повернутись назад");
        keyboardRows.add(keyboardButtons);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }
}