package com.example.universitytelegrambot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "adsTable")
@Data
public class Ads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ad;
}
