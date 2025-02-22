package ru.vsu.cs.masalkin.messaging.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.masalkin.messaging.ConsumerService;
import ru.vsu.cs.masalkin.service.StartService;

import static ru.vsu.cs.masalkin.configuration.RabbitQueue.CALLBACK_MESSAGE_UPDATE;
import static ru.vsu.cs.masalkin.configuration.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final StartService startService;

    public ConsumerServiceImpl(StartService startService) {
        this.startService = startService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        startService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = CALLBACK_MESSAGE_UPDATE)
    public void consumeCallbackMessageUpdates(Update update) {
        startService.processCallbackMessage(update);
    }
}