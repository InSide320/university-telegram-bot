package com.example.universitytelegrambot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardMarkupProvider {

    @Value("${keyboard.row1.button1}")
    private String row1Button1;

    @Value("${keyboard.row1.button2}")
    private String row1Button2;

    @Value("${keyboard.row2.button1}")
    private String row2Button1;

    @Value("${keyboard.row2.button2}")
    private String row2Button2;

    @Value("${keyboard.row2.button3}")
    private String row2Button3;


    public ReplyKeyboardMarkup createKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(row1Button1);
        row1.add(row1Button2);
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(row2Button1);
        row2.add(row2Button2);
        row2.add(row2Button3);
        keyboardRows.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }
}