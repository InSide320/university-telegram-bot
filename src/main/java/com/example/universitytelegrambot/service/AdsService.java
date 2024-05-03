package com.example.universitytelegrambot.service;

import com.example.universitytelegrambot.model.Ads;
import com.example.universitytelegrambot.model.AdsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdsService {

    private final AdsRepository adsRepository;

    public AdsService(AdsRepository adsRepository) {
        this.adsRepository = adsRepository;
    }

    public Iterable<Ads> findAll() {
        return adsRepository.findAll();
    }
}
