package ru.vsu.cs.masalkin.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.masalkin.service.ConsumerService;
import ru.vsu.cs.masalkin.service.MainService;

import static ru.vsu.cs.masalkin.RabbitQueue.CALLBACK_MESSAGE_UPDATE;
import static ru.vsu.cs.masalkin.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {

    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = CALLBACK_MESSAGE_UPDATE)
    public void consumeCallbackMessageUpdates(Update update) {
        log.debug("NODE: CallbackQuery message is received");
        mainService.processCallbackMessage(update);
    }
}
