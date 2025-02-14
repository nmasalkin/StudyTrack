package ru.vsu.cs.masalkin.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import ru.vsu.cs.masalkin.controller.UpdateController;
import ru.vsu.cs.masalkin.service.AnswerConsumer;

import static ru.vsu.cs.masalkin.RabbitQueue.*;

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
}
