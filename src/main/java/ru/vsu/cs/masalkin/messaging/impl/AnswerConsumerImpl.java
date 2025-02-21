package ru.vsu.cs.masalkin.messaging.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.vsu.cs.masalkin.controller.UpdateController;
import ru.vsu.cs.masalkin.messaging.AnswerConsumer;

import static ru.vsu.cs.masalkin.configuration.RabbitQueue.*;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consumeAnswer(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = EDIT_ANSWER_MESSAGE)
    public void consumeEditAnswer(EditMessageText editMessageText) {
        updateController.setView(editMessageText);
    }

    @Override
    @RabbitListener(queues = DELETE_MESSAGE)
    public void consumeDeleteMessage(DeleteMessage deleteMessage) {
        updateController.setView(deleteMessage);
    }
}