package ru.vsu.cs.masalkin.messaging.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.masalkin.messaging.UpdateProducer;

@Service
@Log4j
public class UpdateProducerImpl implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void produce(String rabbitQueue, Update update) {
        if (update.hasCallbackQuery()) {
            log.debug(update.getCallbackQuery().getData());
        }
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}