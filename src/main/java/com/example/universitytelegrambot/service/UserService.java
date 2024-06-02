package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.User;
import com.example.universitytelegrambot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    public String registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUsername(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("User saved: {}", user);
            return EmojiParser.parseToUnicode("Користувача було додано!" + ":white_check_mark:" + "\n" +
                    "Перевірити персональні дані: /mydata");
        } else {
            log.info("User already exists: {}", userRepository.findById(msg.getChatId()));
            return EmojiParser.parseToUnicode("Користувач вже існує!" + "" + "\n" +
                    "Перевірити персональні дані: /mydata");
        }
    }
}
