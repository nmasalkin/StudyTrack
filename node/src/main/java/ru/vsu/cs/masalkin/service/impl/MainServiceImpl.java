package ru.vsu.cs.masalkin.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vsu.cs.masalkin.service.MainService;
import ru.vsu.cs.masalkin.service.ProducerService;

import java.util.ArrayList;
import java.util.List;

import static ru.vsu.cs.masalkin.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {

    private final ProducerService producerService;

    public MainServiceImpl(ProducerService producerService) {
        this.producerService = producerService;
    }

    public void processTextMessage(Update update) {
        var text = update.getMessage().getText();

        if (START.equals(text)) {
            startProcess(update.getMessage().getChatId());
            log.debug("Start command received " + update);
        } else {
            log.error("Unknown command " + text);
            var output = "Неизвестная команда";
            sendAnswer(output, update.getMessage().getChatId());
        }
    }

    @Override
    public void processCallbackMessage(Update update) {
        var data = update.getCallbackQuery().getData();

        if (HELP.equals(data)) {
            helpProcess(update.getMessage().getChatId());
        } else if (REGISTRATION.equals(data)) {
            registrationProcess(update.getCallbackQuery().getMessage().getChatId());
        } else if (TOGGLE_NOTIFICATION.equals(data)) {
            toggleNotificationProcess(update.getCallbackQuery().getMessage().getChatId());
        } else {
            log.error("Unknown command " + data);
            var output = "Неизвестная команда";
            sendAnswer(output, update.getCallbackQuery().getMessage().getChatId());
        }
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private void toggleNotificationProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("В данный момент уведомления недоступны");
        producerService.produceAnswer(sendMessage);
    }

    private void registrationProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("В данный момент регистрация недоступна");
        producerService.produceAnswer(sendMessage);
    }

    private void helpProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Тебе уже не поможешь(");
        producerService.produceAnswer(sendMessage);
    }

    private void startProcess (Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Приветствую! Выберите команду");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Регистрация");
        inlineKeyboardButton1.setCallbackData("/registration");
        lineOfButtons1.add(inlineKeyboardButton1);

        List<InlineKeyboardButton> lineOfButtons2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Вкл/выкл уведомления");
        inlineKeyboardButton2.setCallbackData("/toggle_notification");
        lineOfButtons2.add(inlineKeyboardButton2);

        List<InlineKeyboardButton> lineOfButtons3 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Помощь");
        inlineKeyboardButton3.setUrl("https://yandex.ru/maps/org/voronezhskaya_oblastnaya_klinicheskaya_psikhiatricheskaya_bolnitsa/73071523277/?ll=38.997923%2C51.604175&z=14");
        lineOfButtons3.add(inlineKeyboardButton3);

        listOfButtons.add(lineOfButtons1);
        listOfButtons.add(lineOfButtons2);
        listOfButtons.add(lineOfButtons3);

        markupInline.setKeyboard(listOfButtons);
        sendMessage.setReplyMarkup(markupInline);

        producerService.produceAnswer(sendMessage);
    }
}
