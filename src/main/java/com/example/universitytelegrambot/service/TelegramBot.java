package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.config.BotConfig;
import com.example.universitytelegrambot.model.Ads;
import com.example.universitytelegrambot.model.User;
import com.example.universitytelegrambot.model.documents.AdmissionDocuments;
import com.example.universitytelegrambot.model.documents.AdmissionDocumentsRepository;
import com.example.universitytelegrambot.model.faculty.EducationLevelRepository;
import com.example.universitytelegrambot.model.faculty.Faculty;
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
import java.util.Optional;

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
    private final AdmissionDocumentsRepository admissionDocumentsRepository;

    public TelegramBot(
            BotConfig config,
            BotCommandProvider botCommandProvider,
            UserService userService,
            KeyboardMarkupProvider keyboardMarkupProvider, AdsService adsService, DataLoader dataLoader, FacultyRepository facultyRepository, EducationLevelRepository educationLevelRepository, EducationLevelRepository educationLevelRepository1, SpecialtyRepository specialtyRepository, AdmissionDocumentsRepository admissionDocumentsRepository) {
        this.config = config;
        this.userService = userService;
        this.keyboardMarkupProvider = keyboardMarkupProvider;
        this.adsService = adsService;
        this.dataLoader = dataLoader;
        this.facultyRepository = facultyRepository;
        this.educationLevelRepository = educationLevelRepository1;
        this.admissionDocumentsRepository = admissionDocumentsRepository;

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
                String text = "You press 'YES' button\n" +
                        userService.registerUser(update.getCallbackQuery().getMessage());
                executeEditMessageText(text, chatId, messageId);
            } else if (callbackData.equals(NO_BUTTON)) {
                String text = "You press 'NO' button and you don't want to send your data";
                executeEditMessageText(text, chatId, messageId);
            }
        }
    }

    private void handleMessage(Message message) {
        String messageText = message.getText();
        long chatId = message.getChatId();

        if (config.getOwnerId() != chatId) {
            return;
        }

        if (messageText.startsWith("/send")) {
            sendMessagesToAllUsers(messageText, chatId);
        } else if (messageText.startsWith("/create_ad")) {
            createAd(messageText, chatId);
        } else if (messageText.startsWith("/delete_ad")) {
            deleteAd(messageText, chatId);
        } else {
            handleOtherCommands(messageText, chatId, message);
        }
    }

    private void sendMessagesToAllUsers(String messageText, long chatId) {
        String textToSend = extractTextToSend(messageText);
        if (textToSend != null) {
            userService.findAll().forEach(user -> prepareAndSendMessage(user.getChatId(), textToSend));
        } else {
            prepareAndSendMessage(chatId, "You need to set text");
        }
    }

    private String extractTextToSend(String messageText) {
        int index = messageText.indexOf(" ");
        return index != -1 ? EmojiParser.parseToUnicode(messageText.substring(index)) : null;
    }

    private void createAd(String messageText, long chatId) {
        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String adText = parts[1].trim();
            if (!adText.isEmpty()) {
                adsService.createAd(adText);
                prepareAndSendMessage(chatId, "Ad created successfully!");
            } else {
                prepareAndSendMessage(chatId, "You need to set text for the ad");
            }
        } else {
            prepareAndSendMessage(chatId, "Usage: /create_ad <ad text>");
        }
    }

    private void deleteAd(String messageText, long chatId) {
        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String adIdText = parts[1].trim();
            try {
                Long adId = Long.valueOf(adIdText);
                adsService.deleteAd(adId);
                prepareAndSendMessage(chatId, "Ad with ID " + adId + " has been deleted successfully!");
            } catch (NumberFormatException e) {
                prepareAndSendMessage(chatId, "Invalid ID format. Please provide a valid numeric ID.");
            }
        } else {
            prepareAndSendMessage(chatId, "Usage: /delete_ad <ID>");
        }
    }

    private void handleOtherCommands(String messageText, long chatId, Message message) {
        switch (messageText) {
            case "/start":
                startCommandReceived(chatId, message.getChat().getUserName());
                loadData();
                break;
            case "/help":
                prepareAndSendMessage(chatId, HELP_TEXT);
                break;
            case "/mydata", "check my data":
                sendUserData(chatId);
                break;
            case "register", "/register":
                register(chatId, message);
                break;
            case "/deletedata", "delete my data":
                deleteUserData(chatId);
                break;
            case "faculties in CHDTU":
                sendFaculties(chatId);
                break;
            case "documents for admission":
                sendDocumentsForAdmission(chatId);
                break;
            default:
                prepareAndSendMessage(chatId, "Sorry, command was not recognized");
                break;
        }
    }

    private void sendDocumentsForAdmission(long chatId) {
        List<AdmissionDocuments> documents = admissionDocumentsRepository.findAll();
        for (AdmissionDocuments document : documents) {
            var documentMessage = EmojiParser.parseToUnicode("        При подачі документів і виконання вимог до зарахування вступник подає :\n" +
                    ":exclamation:" + document.getIdentificationDocumentCopy() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getTaxpayerIdentificationCopy() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getSpecialConditionsDocumentsCopies() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getMilitaryRegistrationDocumentCopy() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getPreviousEducationDocumentCopy() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getExternalEvaluationCertificate() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getUkrainianLanguageZNOResults() + ":page_facing_up:" + "\n\n" +
                    ":exclamation:" + document.getPhotographs() + ":camera:" + "\n\n" +
                    ":exclamation:" + document.getFolderWithFiles() + ":open_file_folder:" + "\n\n" +
                    ":exclamation:" + document.getEnvelopesAndFiles()  + ":open_file_folder:" + "\n\n"
            );
            prepareAndSendMessage(chatId, documentMessage);
        }
    }

    private void loadData() {
        facultyRepository.saveAllAndFlush(dataLoader.loadFacultiesFromFile("db/faculties.json"));
        educationLevelRepository.saveAllAndFlush(dataLoader.loadEducationLevelsFromFile("db/education_levels.json"));
        specialtyRepository.saveAllAndFlush(dataLoader.loadSpecialtiesFromFile("db/specialties.json"));
        admissionDocumentsRepository.saveAllAndFlush(dataLoader.loadAdmissionDocumentsFromFile("db/documents.json"));
    }

    private void sendFaculties(long chatId) {
        List<Faculty> faculties = facultyRepository.findAll();
        for (Faculty faculty : faculties) {
            prepareAndSendMessage(chatId, faculty.getName());
        }
    }

    private void deleteUserData(long chatId) {
        userService.deleteUserById(chatId);
        prepareAndSendMessage(chatId, "Your data has been deleted");
    }

    private void sendUserData(Long chatId) {
        Optional<User> userOptional = userService.findById(chatId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            var messageToSend = "Full name: " + user.getFirstName() + " " + user.getLastName() +
                    "\n" +
                    "Username: " + user.getUsername() +
                    "\n" +
                    "Registered at: " + user.getRegisteredAt();
            prepareAndSendMessage(chatId, messageToSend);
        } else {
            prepareAndSendMessage(chatId, "User not found, please register first.");
        }
    }

    private void register(Long chatId, Message msg) {
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
            String adMessage = ad.getAd(); // Отримання тексту оголошення

            for (User user : users) {
                prepareAndSendMessage(user.getChatId(), adMessage);
            }
        }
    }
}
