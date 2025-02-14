package ru.vsu.cs.masalkin.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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
        } else if (MENU.equals(text)) {
            menuProcess(update.getMessage().getChatId());
            log.debug("Menu command received " + update);
        } else {
            log.error("Unknown command " + text);
            var output = "Неизвестная команда";
            sendAnswer(output, update.getMessage().getChatId());
        }
    }

    @Override
    public void processCallbackMessage(Update update) {
        var data = update.getCallbackQuery().getData();

        if (START.equals(data)) {
            startProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            log.debug("Start command received " + update);
        } else if (REGISTRATION.equals(data)) {
            registrationProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
        } else if (ABOUT_BOT.equals(data)) {
            aboutBotProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
        } else if (MENU.equals(data)) {
            menuProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
        } else if (SEMESTER_LIST.equals(data)) {
            chooseSemesterProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
        } else if (TOGGLE_NOTIFICATION.equals(data)) {
            toggleNotificationProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
        } else if (data.contains("/semester")) {
            semesterProcess(Integer.parseInt(data.replace("/semester_", "")), update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
        } else {
            log.error("Unknown command " + data);
            sendAnswer("Неизвестная команда", update.getCallbackQuery().getMessage().getChatId());
        }
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private void startProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("👋Приветствую! Для того чтобы начать пользоваться данным ботом🤖, необходимо пройти регистрацию😁");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Регистрация");
        inlineKeyboardButton1.setCallbackData("/registration");
        lineOfButtons1.add(inlineKeyboardButton1);

        List<InlineKeyboardButton> lineOfButtons2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Что может бот?");
        inlineKeyboardButton2.setCallbackData("/about_bot");
        lineOfButtons2.add(inlineKeyboardButton2);

        listOfButtons.add(lineOfButtons1);
        listOfButtons.add(lineOfButtons2);

        markupInline.setKeyboard(listOfButtons);
        sendMessage.setReplyMarkup(markupInline);

        producerService.produceAnswer(sendMessage);
    }

    private void startProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("👋Приветствую! Для того чтобы начать пользоваться данным ботом🤖, необходимо пройти регистрацию😁");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Регистрация");
        inlineKeyboardButton1.setCallbackData("/registration");
        lineOfButtons1.add(inlineKeyboardButton1);

        List<InlineKeyboardButton> lineOfButtons2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Что может бот?");
        inlineKeyboardButton2.setCallbackData("/about_bot");
        lineOfButtons2.add(inlineKeyboardButton2);

        listOfButtons.add(lineOfButtons1);
        listOfButtons.add(lineOfButtons2);

        markupInline.setKeyboard(listOfButtons);
        editMessageText.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessageText);
    }

    private void registrationProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("В данный момент регистрация недоступна");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Вернуться в начало");
        inlineKeyboardButton.setCallbackData("/start");
        lineOfButtons.add(inlineKeyboardButton);
        listOfButtons.add(lineOfButtons);

        markupInline.setKeyboard(listOfButtons);
        editMessageText.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessageText);
    }

    private void aboutBotProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Данный бот создан для удобного просмотра оценок студентов");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Вернуться в начало");
        inlineKeyboardButton.setCallbackData("/start");
        lineOfButtons.add(inlineKeyboardButton);
        listOfButtons.add(lineOfButtons);

        markupInline.setKeyboard(listOfButtons);
        editMessageText.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessageText);
    }

    private void menuProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите желаемое действие");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Список семестров");
        inlineKeyboardButton1.setCallbackData("/semester_list");
        lineOfButtons1.add(inlineKeyboardButton1);
        listOfButtons.add(lineOfButtons1);

        List<InlineKeyboardButton> lineOfButtons2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Вкл/выкл уведомления");
        inlineKeyboardButton2.setCallbackData("/toggle_notification");
        lineOfButtons2.add(inlineKeyboardButton2);
        listOfButtons.add(lineOfButtons2);

        markupInline.setKeyboard(listOfButtons);
        sendMessage.setReplyMarkup(markupInline);

        producerService.produceAnswer(sendMessage);
    }

    private void menuProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("Выберите желаемое действие");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Список семестров");
        inlineKeyboardButton1.setCallbackData("/semester_list");
        lineOfButtons1.add(inlineKeyboardButton1);
        listOfButtons.add(lineOfButtons1);

        List<InlineKeyboardButton> lineOfButtons2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Вкл/выкл уведомления");
        inlineKeyboardButton2.setCallbackData("/toggle_notification");
        lineOfButtons2.add(inlineKeyboardButton2);
        listOfButtons.add(lineOfButtons2);

        markupInline.setKeyboard(listOfButtons);
        editMessageText.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessageText);
    }

    private void chooseSemesterProcess(Long chatId, Integer messageId) {
        var editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText("Выберите семестр из списка");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        for (int i = 8; i > 0; i--) {
            List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Семестр №" + i);
            inlineKeyboardButton.setCallbackData("/semester_" + i);
            lineOfButtons1.add(inlineKeyboardButton);

            listOfButtons.add(lineOfButtons1);
        }

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Вернуться в меню");
        inlineKeyboardButton1.setCallbackData("/menu");
        lineOfButtons1.add(inlineKeyboardButton1);
        listOfButtons.add(lineOfButtons1);

        markupInline.setKeyboard(listOfButtons);
        editMessage.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessage);
    }

    private void semesterProcess(int semesterNumber, Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);

        editMessageText.setParseMode("MarkdownV2");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("*__Ваши оценки за %d семестр:__*\n\n", semesterNumber));
        for (int i = 1; i < 4; i++) {
            stringBuilder.append(String.format("                    *%s*:\n" +
                                               "            Атт1:  *%s*,    Атт2:  *%s*,    Атт3:  *%s*,\n" +
                                               "Посещаемость:  *%s*,   Итог:  *%s*",
                    "Иностранный язык:",
                    "50" != null ? "_50_" : "~   ~", "50" != null ? "_50_" : "~   ~", null != null ? "_50_" : "~   ~",
                    "100" != null ? "_100_" : "~   ~", "100" != null ? "_100\\(Отлично\\)_" : "~   ~"));
            stringBuilder.append("\n\n");
        }
        editMessageText.setText(stringBuilder.toString());

        if (semesterNumber == 1) {
            editMessageText.setText("В данный момент просмотр оценок недоступен");
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Вернуться в меню");
        inlineKeyboardButton1.setCallbackData("/menu");
        lineOfButtons1.add(inlineKeyboardButton1);
        listOfButtons.add(lineOfButtons1);

        markupInline.setKeyboard(listOfButtons);
        editMessageText.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessageText);
    }

    private void toggleNotificationProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText("В данный момент уведомления недоступны");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Вернуться в меню");
        inlineKeyboardButton1.setCallbackData("/menu");
        lineOfButtons1.add(inlineKeyboardButton1);
        listOfButtons.add(lineOfButtons1);

        markupInline.setKeyboard(listOfButtons);
        editMessageText.setReplyMarkup(markupInline);

        producerService.produceEditAnswer(editMessageText);
    }
}