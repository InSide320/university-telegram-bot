package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.config.BotConfig;
import com.example.universitytelegrambot.model.Ads;
import com.example.universitytelegrambot.model.User;
import com.example.universitytelegrambot.model.faculty.EducationLevelRepository;
import com.example.universitytelegrambot.model.faculty.FacultyRepository;
import com.example.universitytelegrambot.model.faculty.SpecialtyRepository;
import com.example.universitytelegrambot.provider.BotCommandProvider;
import com.example.universitytelegrambot.provider.KeyboardMarkupProvider;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public static final String HELP_TEXT =
            "This bot is created to demonstrate university telegram bot." +
                    "\n\n" +
                    "You can execute commands from the main on the left or by typing a command:" +
                    "\n\n" + "Type /start to see welcome message\n\n" +
                    "Type /mydata to see data store about yourself\n\n" +
                    "Type /help to see this message again";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";
    static final String ERROR_MESSAGE = "Error occurred: {}";

    private final KeyboardMarkupProvider keyboardMarkupProvider;
    private final UserService userService;
    private final AdsService adsService;

    private final DataLoader dataLoader;
    private final FacultyRepository facultyRepository;
    private final EducationLevelRepository educationLevelRepository;
    private final SpecialtyRepository specialtyRepository;

    public TelegramBot(
            BotConfig config,
            BotCommandProvider botCommandProvider,
            UserService userService,
            KeyboardMarkupProvider keyboardMarkupProvider, AdsService adsService, DataLoader dataLoader, FacultyRepository facultyRepository, EducationLevelRepository educationLevelRepository, EducationLevelRepository educationLevelRepository1, SpecialtyRepository specialtyRepository) {
        this.config = config;
        this.userService = userService;
        this.keyboardMarkupProvider = keyboardMarkupProvider;
        this.adsService = adsService;
        this.dataLoader = dataLoader;
        this.facultyRepository = facultyRepository;
        this.educationLevelRepository = educationLevelRepository1;

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
        this.specialtyRepository = specialtyRepository;
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
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals(YES_BUTTON)) {
                String text = "You press YES button";
                executeEditMessageText(text, chatId, messageId);
            } else if (callbackData.equals(NO_BUTTON)) {
                String text = "You press NO button";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    private void handleMessage(Message message) {
        String messageText = message.getText();
        long chatId = message.getChatId();

        if (messageText.contains("/send") && config.getOwnerId() == chatId) {
            int index = messageText.indexOf(" ");

            if (index != -1) {
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(index));
                var users = userService.findAll();
                for (User user : users) {
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            } else {
                prepareAndSendMessage(chatId, "You need to set text");
            }
        } else {
            switch (messageText) {
                case "/start" -> {
                    userService.registerUser(message);
                    startCommandReceived(chatId, message.getChat().getUserName());
                    facultyRepository.saveAllAndFlush(dataLoader.loadFacultiesFromFile("db/faculties.json"));
                    educationLevelRepository.saveAllAndFlush(dataLoader.loadEducationLevelsFromFile("db/education_levels.json"));
                    specialtyRepository.saveAllAndFlush(dataLoader.loadSpecialtiesFromFile("db/specialties.json"));
                }
                case "/help" -> prepareAndSendMessage(chatId, HELP_TEXT);
                case "/register" -> register(chatId);
                default -> prepareAndSendMessage(chatId, "Sorry, command was not recognized");
            }
        }
    }

    private void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = getLists();
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        executeMessage(message);
    }

    private static List<List<InlineKeyboardButton>> getLists() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Yes");
        yesButton.setCallbackData(YES_BUTTON);

        var noButton = new InlineKeyboardButton();

        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInline.add(rowInline);
        return rowsInline;
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText(text);
        editMessageText.setMessageId((int) messageId);

        executeMessage(editMessageText);
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + ":blush:");
        log.info("Replayed to user {}", name);

        sendMessageWithKeyboard(chatId, answer);
    }

    public void sendMessageWithKeyboard(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        message.setReplyMarkup(keyboardMarkupProvider.createKeyboardMarkup());

        executeMessage(message);
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e);
        }
    }

    private void executeMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e);
        }
    }

    private void handleTelegramApiException(TelegramApiException e) {
        log.error(ERROR_MESSAGE, e.getMessage());
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendAds() {
        var ads = adsService.findAll();
        var users = userService.findAll();

        for (Ads ad : ads) {
            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }

}
