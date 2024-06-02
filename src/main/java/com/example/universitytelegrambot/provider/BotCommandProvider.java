package com.example.universitytelegrambot.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotCommandProvider {

    private final List<BotCommand> listOfCommands;

    public BotCommandProvider(@Value("${bot.commands}") List<String> commandStrings) {
        this.listOfCommands = new ArrayList<>();
        for (String commandString : commandStrings) {
            String[] parts = commandString.split(":");
            if (parts.length == 2) {
                listOfCommands.add(new BotCommand(parts[0], parts[1]));
            }
        }
    }

    public List<BotCommand> listOfCommands() {
        return listOfCommands;
    }
}
