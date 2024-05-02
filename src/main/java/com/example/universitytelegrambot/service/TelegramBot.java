package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.config.BotConfig;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public static final String HELP_TEXT =
            "This bot is created to demonstrate university telegram bot." +
                    "\n\n" +
                    "You can execute commands from the main on the left or by typing a command:" +
                    "\n\n" + "Type /start to see welcome message\n\n" +
                    "Type /mydata to see data store about yourself\n\n" +
                    "Type /help to see this message again";

    private final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    private final KeyboardMarkupProvider keyboardMarkupProvider;
    private final UserRegistrationService userRegistrationService;


    public TelegramBot(
            BotConfig config,
            BotCommandProvider botCommandProvider,
            UserRegistrationService userRegistrationService,
            KeyboardMarkupProvider keyboardMarkupProvider) {
        this.config = config;
        this.userRegistrationService = userRegistrationService;
        this.keyboardMarkupProvider = keyboardMarkupProvider;

        try {
            this.execute(
                    new SetMyCommands(
                            botCommandProvider.listOfCommands(),
                            new BotCommandScopeDefault(),
                            null)
            );
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: {}", e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleMessage(Message message) {
        String messageText = message.getText();
        long chatId = message.getChatId();
        switch (messageText) {
            case "/start":
                userRegistrationService.registerUser(message);
                startCommandReceived(chatId, message.getChat().getUserName());
                break;
            case "/help":
                sendMessage(chatId, HELP_TEXT);
                break;
            default:
                sendMessage(chatId, "Sorry, command was not recognized");
                break;
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + ":blush:");
        log.info("Replayed to user {}", name);
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(keyboardMarkupProvider.createKeyboardMarkup());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }
}
