package com.example.universitytelegrambot.controller;

import com.example.universitytelegrambot.config.BotConfig;
import com.example.universitytelegrambot.constant.TelegramConstantVariable;
import com.example.universitytelegrambot.error.HandleTelegramError;
import com.example.universitytelegrambot.model.Ads;
import com.example.universitytelegrambot.model.User;
import com.example.universitytelegrambot.model.documents.AdmissionDocuments;
import com.example.universitytelegrambot.model.documents.AdmissionDocumentsRepository;
import com.example.universitytelegrambot.model.faculty.*;
import com.example.universitytelegrambot.provider.BotCommandProvider;
import com.example.universitytelegrambot.provider.CreateInlineKeyboardButtonProvider;
import com.example.universitytelegrambot.provider.DataLoaderProvider;
import com.example.universitytelegrambot.provider.KeyboardMarkupProvider;
import com.example.universitytelegrambot.service.AdsService;
import com.example.universitytelegrambot.service.SpecialityService;
import com.example.universitytelegrambot.service.UserService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    private final UserService userService;
    private final AdsService adsService;

    private final DataLoaderProvider dataLoaderProvider;
    private final FacultyRepository facultyRepository;
    private final SpecialtyRepository specialtyRepository;
    private final SpecialityService specialityService;
    private final KeyboardMarkupProvider keyboardMarkupProvider;
    private final HandleTelegramError handleTelegramError;
    private final AdmissionDocumentsRepository admissionDocumentsRepository;
    private final DepartmentRepository departmentRepository;
    private final CreateInlineKeyboardButtonProvider createInlineKeyboardButtonProvider;

    private static final String HELP_TEXT = new StringBuilder()
            .append("This bot is created to demonstrate university telegram bot.")
            .append("\n\n")
            .append("You can execute commands from the main on the left or by typing a command:")
            .append("\n\n").append("Type /start to see welcome message\n\n")
            .append("Type /mydata to see data store about yourself\n\n")
            .append("Type /help to see this message again").toString();

    public TelegramBot(
            BotConfig config,
            BotCommandProvider botCommandProvider,
            UserService userService,
            AdsService adsService, DataLoaderProvider dataLoaderProvider,
            FacultyRepository facultyRepository,
            SpecialtyRepository specialtyRepository,
            SpecialityService specialityService, KeyboardMarkupProvider keyboardMarkupProvider,
            HandleTelegramError handleTelegramError,
            AdmissionDocumentsRepository admissionDocumentsRepository, DepartmentRepository departmentRepository, CreateInlineKeyboardButtonProvider createInlineKeyboardButtonProvider) {
        this.config = config;
        this.userService = userService;
        this.adsService = adsService;
        this.dataLoaderProvider = dataLoaderProvider;
        this.facultyRepository = facultyRepository;
        this.specialityService = specialityService;
        this.keyboardMarkupProvider = keyboardMarkupProvider;
        this.handleTelegramError = handleTelegramError;
        this.admissionDocumentsRepository = admissionDocumentsRepository;
        this.specialtyRepository = specialtyRepository;
        this.departmentRepository = departmentRepository;
        this.createInlineKeyboardButtonProvider = createInlineKeyboardButtonProvider;

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
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleMessage(Message message) {
        String messageText = message.getText();
        long chatId = message.getChatId();

        if (config.getOwnerId() != chatId) {
            return;
        }

        if (messageText.matches("^/send.*")) {
            sendMessagesToAllUsers(messageText, chatId);
        } else if (messageText.matches("^/create_ad.*")) {
            createAd(messageText, chatId);
        } else if (messageText.matches("^/delete_ad.*")) {
            deleteAd(messageText, chatId);
        } else {
            handleOtherCommands(messageText, chatId, message);
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long messageId = callbackQuery.getMessage().getMessageId();
        long chatId = callbackQuery.getMessage().getChatId();

        if (callbackData.startsWith(TelegramConstantVariable.PREFIX_TO_CALLBACK_SPECIALTY)) {
            handleSpecialtyCallback(callbackData, chatId, messageId);
        } else if (callbackData.startsWith(TelegramConstantVariable.PREFIX_TO_CALLBACK_DEPARTMENT)) {
            handleDepartmentCallback(callbackData, chatId, messageId);
        } else {
            handleOtherCallbacks(callbackData, callbackQuery, chatId, messageId);
        }
    }

    private void handleSpecialtyCallback(String callbackData, long chatId, long messageId) {
        long specialtyId = Long.parseLong(callbackData.substring(TelegramConstantVariable.PREFIX_TO_CALLBACK_SPECIALTY.length()));
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new IllegalStateException("Specialty not found with id: " + specialtyId));
        String text = specialityService.sendSpecialityMessage(specialty);

        executeEditMessageText(text, chatId, messageId);
    }

    private void handleDepartmentCallback(String callbackData, long chatId, long messageId) {
        long departmentId = Long.parseLong(callbackData.substring(TelegramConstantVariable.PREFIX_TO_CALLBACK_DEPARTMENT.length()));
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalStateException("Department not found with id: " + departmentId));
        String text = EmojiParser.parseToUnicode(
                department.getDescription() +
                        "<b>Посилання для більш детального ознайомлення:</b>\n" +
                        "<b><a href=\"" + department.getLink() + "\">" + department.getName() + "</a></b>\n"
        );

        executeEditMessageText(text, chatId, messageId);
    }

    private void handleOtherCallbacks(String callbackData, CallbackQuery callbackQuery, long chatId, long messageId) {
        switch (callbackData) {
            case TelegramConstantVariable.YES_BUTTON -> {
                String text = "Ви нажали 'ТАК' кнопку\n" +
                        userService.registerUser(callbackQuery.getMessage());
                executeEditMessageText(text, chatId, messageId);
            }
            case TelegramConstantVariable.NO_BUTTON -> {
                String text = "Ви нажали кнопку 'НІ' тим самим, відмовившись від надсилання своїх даних";
                executeEditMessageText(text, chatId, messageId);
            }
            case TelegramConstantVariable.BACHELORS_LEVEL ->
                    aboutSpecificSpecialty(chatId, TelegramConstantVariable.BACHELORS_LEVEL, messageId);
            case TelegramConstantVariable.MASTERS_LEVEL ->
                    aboutSpecificSpecialty(chatId, TelegramConstantVariable.MASTERS_LEVEL, messageId);
            case TelegramConstantVariable.BACK_TO_EDUCATION_LEVEL ->
                    updateEducationMessage(chatId, messageId, "Оберіть освітній рівень:");
            default -> throw new IllegalStateException("Unexpected value: " + callbackData);
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
        if (!messageText.startsWith("/create_ad")) {
            prepareAndSendMessage(chatId, "Usage: /create_ad <ad text>");
            return;
        }

        String[] parts = messageText.split(" ", 2);
        if (parts.length == 2) {
            String adText = parts[1].trim();
            if (!adText.isEmpty()) {
                adsService.createAd(adText);
                prepareAndSendMessage(chatId, "Ad created successfully!");
                return;
            }
        }

        prepareAndSendMessage(chatId, "You need to set text for the ad");
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
                dataLoaderProvider.loadData();
                userService.registerUser(message);
                startCommandReceived(chatId, message.getChat().getUserName());
                break;
            case "/help", "Допомога":
                prepareAndSendMessage(chatId, HELP_TEXT);
                break;
            case "/mydata", "Перевірка персональних даних":
                sendUserData(chatId);
                break;
            case "Реєстрація користувача", "/register":
                register(chatId);
                break;
            case "/deletedata", "Видалити персональні дані":
                deleteUserData(chatId);
                break;
            case "Спеціальності фактультету":
                updateEducationMessage(chatId, null, "");
                break;
            case "Документи для вступу":
                sendDocumentsForAdmission(chatId);
                break;
            case "Перелік кафедр":
                aboutSpecificDepartments(chatId);
                break;
            case "Про факультет":
                informationAboutFaculty(chatId);
                break;
            default:
                prepareAndSendMessage(chatId, "Sorry, command was not recognized");
                break;
        }
    }

    private void informationAboutFaculty(long chatId) {
        var checkMark = EmojiParser.parseToUnicode(":white_check_mark:");
        var infoMark = EmojiParser.parseToUnicode(":information_source:");
        var departments = departmentRepository.findAll();
        StringBuilder text = new StringBuilder(
                EmojiParser.parseToUnicode(
                        "Факультет <b>ФІТІС</b> динамічно розвивається та модернізується.\n\n" +
                                "З 2016 року на факультеті <b>Інформаційних технологій і систем</b> " +
                                "проходить апробацію гібридна модель сучасної освіти в галузі ІТ, " +
                                "за якої підвищується вплив та вклад роботодавців галузі в процес підготовки ІТ – спеціалістів.\n\n" +
                                "На сьогоднішній день факультет складається з " + departments.size() + " кафедр:\n")
        );

        for (Department department : departments) {
            text.append("<b>").append(infoMark).append(" ").append(department.getName()).append("</b>\n\n");
        }


        text.append("Факультет плідно співпрацює із багатьма компаніями, зокрема: " +
                "<b>" + checkMark + " «Доктор Елекс»\n" +
                checkMark + " «АЛТ Україна Лтд» (МІС “ЕМСІМЕД”)\n" +
                checkMark + " «CoreValue»\n" +
                checkMark + " «MOZI development»\n" +
                checkMark + " «Andersen»\n" +
                checkMark + " «Ukrainian Hi-Tech Initiative»\n" +
                checkMark + " «QATestLab»\n" +
                checkMark + " Cisco Systems Inc\n" +
                checkMark + " «Теко Трейд»\n" +
                checkMark + " «TRIARE»\n" +
                checkMark + " «eKreative»\n" +
                checkMark + " «Master of Code»\n" +
                checkMark + " «InterLink»\n" +
                checkMark + " «SPD-Ukraine»\n" +
                checkMark + " ІТ-кластером Черкас (Cherkasy IT Cluster)." +
                "</b>\n\n" +
                "Для більш детального ознаймлення, можна перейти на портал сайту:" +
                "<a href=\"https://fitis.chdtu.edu.ua\">Факультет інформаційних технологій і систем</a>"
        );

        prepareAndSendMessage(chatId, text.toString());
    }

    private void aboutSpecificDepartments(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Оберіть зацікавлену Вами кафедру для більш детального ознайомлення:");
        sendMessage.setParseMode("HTML");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline =
                createInlineKeyboardButtonProvider.sayAdditionalInfoAboutDepartments(
                        departmentRepository.findAll()
                );
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        executeMessage(sendMessage);
    }

    private void sendDocumentsForAdmission(long chatId) {
        List<AdmissionDocuments> documents = admissionDocumentsRepository.findAll();
        String exclamationEmoji = ":exclamation:";
        String pageFacingUpEmoji = ":page_facing_up:";
        String openFileFolder = ":open_file_folder:";
        String camera = ":camera:";

        for (AdmissionDocuments document : documents) {
            var documentMessage = EmojiParser.parseToUnicode("        При подачі документів і виконання вимог до зарахування вступник подає :\n" +
                    exclamationEmoji + document.getIdentificationDocumentCopy() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getTaxpayerIdentificationCopy() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getSpecialConditionsDocumentsCopies() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getMilitaryRegistrationDocumentCopy() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getPreviousEducationDocumentCopy() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getExternalEvaluationCertificate() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getUkrainianLanguageZNOResults() + pageFacingUpEmoji + "\n\n" +
                    exclamationEmoji + document.getPhotographs() + camera + "\n\n" +
                    exclamationEmoji + document.getFolderWithFiles() + openFileFolder + "\n\n" +
                    exclamationEmoji + document.getEnvelopesAndFiles() + openFileFolder + "\n\n"
            );
            prepareAndSendMessage(chatId, documentMessage);
        }
    }

    private void deleteUserData(long chatId) {
        userService.deleteUserById(chatId);
        prepareAndSendMessage(chatId, "Ваші дані було видалено!");
    }

    private void sendUserData(Long chatId) {
        Optional<User> userOptional = userService.findById(chatId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            var messageToSend = "Ім'я та прізвище: " + user.getFirstName() + " " + user.getLastName() +
                    "\n" +
                    "Нікнейм: " + user.getUsername() +
                    "\n" +
                    "Зареєстровано в: " + user.getRegisteredAt();
            prepareAndSendMessage(chatId, messageToSend);
        } else {
            prepareAndSendMessage(chatId, "Користувача не було знайдено, спершу зареєструйтесь.");
        }
    }

    private void register(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Ви дійсно хочете надіслати персональні дані для збереження?" +
                "\n" +
                "Ці дані нікуди не надсилаються та ніде не використовуються!");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = createInlineKeyboardButtonProvider.confirmInformation();
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        executeMessage(message);
    }

    private void updateEducationMessage(Long chatId, Long messageId, String message) {
        PartialBotApiMethod<? extends Serializable> method;
        String text;

        if (messageId == null) {
            method = new SendMessage();
            text = "Оберіть освітній рівень:";
        } else {
            method = new EditMessageText();
            ((EditMessageText) method).setMessageId(Math.toIntExact(messageId));
            text = message.isEmpty() ? "Оберіть зацікавлену Вами спеціальність для більш детального ознайомлення:" : message;
        }

        if (method instanceof SendMessage sendMessage) {
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(text);
            sendMessage.setParseMode("HTML");
            sendMessage.setReplyMarkup(new InlineKeyboardMarkup());
        } else {
            EditMessageText editMessageText = (EditMessageText) method;
            editMessageText.setChatId(String.valueOf(chatId));
            editMessageText.setText(text);
            editMessageText.setParseMode("HTML");
            editMessageText.setReplyMarkup(new InlineKeyboardMarkup());
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(createInlineKeyboardButtonProvider.sayEducationalLevel());

        if (method instanceof SendMessage sendMessage) {
            sendMessage.setReplyMarkup(markupInline);
        } else {
            ((EditMessageText) method).setReplyMarkup(markupInline);
        }

        executePartialMethod(method);
    }

    private void aboutSpecificSpecialty(Long chatId, String specialityList, long messageId) {
        EditMessageText editMessageText = setInlineMarkupForEditMessageAboutSpeciality(chatId, specialityList, (int) messageId);

        executeMessage(editMessageText);
    }

    private EditMessageText setInlineMarkupForEditMessageAboutSpeciality(Long chatId, String specialityList, int messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText("Оберіть зацікавлену Вами спеціальність для більш детального ознайомлення:");
        editMessageText.setMessageId(messageId);
        editMessageText.setParseMode("HTML");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline =
                createInlineKeyboardButtonProvider.sayAdditionalInfoAboutSpecialty(
                        specialityService.getSpecialistsByEducationalLevel(specialityList, "Денна")
                );
        markupInline.setKeyboard(rowsInline);
        editMessageText.setReplyMarkup(markupInline);
        return editMessageText;
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText(text);
        editMessageText.setMessageId((int) messageId);
        editMessageText.setParseMode("HTML");

        executeMessage(editMessageText);
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Привіт, " + name + "." + "\n" +
                "Це інформаційний телеграм бот черкаського державного технологічного університету! :classical_building:"
                + "\n" +
                facultyRepository.findAll().get(0).getName()
        );
        log.info("Replayed to user {}", name);

        sendMessageWithKeyboard(chatId, answer);
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

    public void sendMessageWithKeyboard(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(keyboardMarkupProvider.createKeyboardMarkup());
        executeMessage(message);
    }

    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setParseMode("HTML");
        executeMessage(message);
    }

    void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramError.handleTelegramApiException(e);
        }
    }

    public void executeMessage(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            handleTelegramError.handleTelegramApiException(e);
        }
    }

    public void executePartialMethod(PartialBotApiMethod<? extends Serializable> method) {
        try {
            if (method instanceof SendMessage sendMessage) {
                execute(sendMessage);
            } else if (method instanceof EditMessageText editMessageText) {
                execute(editMessageText);
            } else {
                log.error("Unsupported method: PartialBotApiMethod");
            }
        } catch (TelegramApiException e) {
            handleTelegramError.handleTelegramApiException(e);
        }
    }
}
