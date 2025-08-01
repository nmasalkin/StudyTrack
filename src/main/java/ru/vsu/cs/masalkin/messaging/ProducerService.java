package ru.vsu.cs.masalkin.messaging;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ProducerService {
    void produceAnswer(SendMessage message);
    void produceEditAnswer(EditMessageText editMessage);
    void produceDeleteMessage(DeleteMessage deleteMessage);
}