package com.example.universitytelegrambot.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class HandleTelegramError {
    private static final String ERROR_MESSAGE = "Error occurred: {}";

    public void handleTelegramApiException(TelegramApiException e) {
        log.error(ERROR_MESSAGE, e.getMessage());
    }
}
