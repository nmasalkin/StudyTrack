package ru.vsu.cs.masalkin.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ProducerService {
    void produceAnswer(SendMessage message);
    void produceEditAnswer(EditMessageText editMessage);
}
