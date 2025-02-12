package ru.vsu.cs.masalkin.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void produceAnswer(SendMessage message);
}
