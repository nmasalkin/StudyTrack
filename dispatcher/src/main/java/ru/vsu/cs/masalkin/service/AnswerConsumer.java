package ru.vsu.cs.masalkin.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface AnswerConsumer {
    void consumeAnswer(SendMessage sendMessage);
    void consumeEditAnswer(EditMessageText editMessageText);
    void consumeDeleteMessage(DeleteMessage deleteMessage);
}
