package ru.vsu.cs.masalkin.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.masalkin.messaging.UpdateProducer;
import ru.vsu.cs.masalkin.utils.MessageUtils;

import static ru.vsu.cs.masalkin.configuration.RabbitQueue.CALLBACK_MESSAGE_UPDATE;
import static ru.vsu.cs.masalkin.configuration.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;
    private MessageUtils messageUtils;
    private UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else if (update.hasCallbackQuery()) {
            processCallbackMessage(update);
        } else {
            log.error("Received unsupported message type " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            if (update.getMessage().getText().length() > 100){
                setUnsupportedMessageType(update);
            }else {
                processTextMessage(update);
            }
        } else {
            setUnsupportedMessageType(update);
        }
    }

    private void setUnsupportedMessageType(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setView(EditMessageText editMessageText) {
        telegramBot.sendEditMessage(editMessageText);
    }

    public void setView(DeleteMessage deleteMessage) {
        telegramBot.sendDeleteMessage(deleteMessage);
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    private void processCallbackMessage(Update update) {
        updateProducer.produce(CALLBACK_MESSAGE_UPDATE, update);
    }
}