package ru.vsu.cs.masalkin.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vsu.cs.masalkin.entity.SubjectMarks;
import ru.vsu.cs.masalkin.repository.AppUserRepository;
import ru.vsu.cs.masalkin.service.MainService;
import ru.vsu.cs.masalkin.service.ProducerService;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainServiceImpl implements MainService {

    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;

    public MainServiceImpl(ProducerService producerService, AppUserRepository appUserRepository) {
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
    }

    public void menuProcess(Long chatId) {
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

    public void menuProcess(Long chatId, Integer messageId) {
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

    public void chooseSemesterProcess(Long chatId, Integer messageId) {
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

    public void semesterProcess(int semesterNumber, Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        //editMessageText.setParseMode("MarkdownV2");

//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(String.format("*__Ваши оценки за %d семестр:__*\n\n", semesterNumber));

        if (appUserRepository.existsByChatId(chatId)) {
            editMessageText.setText(appUserRepository.findByChatId(chatId).getStudent_marks().toString().substring(0, 1000));
        } else {
            editMessageText.setText("Вы не зарегистрированы");
        }

//        if (subjectMarks.isEmpty()) {
//            editMessageText.setText("У вас нет оценок за данный семестр");
//        } else {
//            for (SubjectMarks subjectMark : subjectMarks) {
//                stringBuilder.append(String.format("                    *%s*:\n" +
//                                                   "Атт1:  *%d*,  Атт2:  *%d*,  Атт3:  *%d*,  Итог:  *%d(%s)*",
//                                                   subjectMark.getSubject_name(), subjectMark.getAtt1(), subjectMark.getAtt2(), subjectMark.getAtt3(), subjectMark.getResult(), subjectMark.getResult5()));
//                stringBuilder.append("\n\n");
//            }
//            editMessageText.setText(stringBuilder.toString());
//        }

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

    public void toggleNotificationProcess(Long chatId, Integer messageId) {
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

    public void aboutBotProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Данный бот создан для удобного просмотра оценок студентов");
        producerService.produceAnswer(sendMessage);
    }
}
