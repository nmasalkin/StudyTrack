package ru.vsu.cs.masalkin.messaging.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.vsu.cs.masalkin.messaging.ProducerService;

import static ru.vsu.cs.masalkin.configuration.RabbitQueue.*;

@Service
public class ProducerServiceImpl implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produceAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void produceEditAnswer(EditMessageText editMessageText) {
        rabbitTemplate.convertAndSend(EDIT_ANSWER_MESSAGE, editMessageText);
    }

    @Override
    public void produceDeleteMessage(DeleteMessage deleteMessage) {
        rabbitTemplate.convertAndSend(DELETE_MESSAGE, deleteMessage);
    }
}