package ru.vsu.cs.masalkin.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface StartService {
    void processTextMessage(Update update);
    void processCallbackMessage(Update update);
}