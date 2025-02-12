package ru.vsu.cs.masalkin.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeTextMessageUpdates(Update update);
    void consumeCallbackMessageUpdates(Update update);
}
