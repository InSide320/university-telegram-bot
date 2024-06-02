package com.example.universitytelegrambot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "userDataTable")
@Data
public class User {
    @Id
    private Long chatId;

    private String username;
    private String firstName;
    private String lastName;
    private Timestamp registeredAt;
}
