package ru.vsu.cs.masalkin.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.entity.SubjectMarks;
import ru.vsu.cs.masalkin.repository.AppUserRepository;
import ru.vsu.cs.masalkin.api.ApiService;
import ru.vsu.cs.masalkin.service.MainService;
import ru.vsu.cs.masalkin.messaging.ProducerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MainServiceImpl implements MainService {

    private final ProducerService producerService;
    private final AppUserRepository appUserRepository;
    private final JsonMapper jsonMapper;
    private final ApiService apiService;

    public MainServiceImpl(ProducerService producerService, AppUserRepository appUserRepository, JsonMapper jsonMapper, ApiService apiService) {
        this.producerService = producerService;
        this.appUserRepository = appUserRepository;
        this.jsonMapper = jsonMapper;
        this.apiService = apiService;
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
        inlineKeyboardButton2.setText("Инфо о студенте");
        inlineKeyboardButton2.setCallbackData("/student_info");
        lineOfButtons2.add(inlineKeyboardButton2);
        listOfButtons.add(lineOfButtons2);

        List<InlineKeyboardButton> lineOfButtons3 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Вкл/выкл уведомления");
        inlineKeyboardButton3.setCallbackData("/toggle_notification");
        lineOfButtons3.add(inlineKeyboardButton3);
        listOfButtons.add(lineOfButtons3);

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

        for (int i = appUserRepository.findByChatId(chatId).getCurrentSemester(); i > 0; i--) {
            List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            if (i == appUserRepository.findByChatId(chatId).getCurrentSemester()) {
                inlineKeyboardButton.setText("Семестр №" + i + " (текущий)");
            } else {
                inlineKeyboardButton.setText("Семестр №" + i);
            }
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
        editMessageText.setParseMode("MarkdownV2");
        if (!appUserRepository.existsByChatId(chatId)) {
            editMessageText.setText("Вы не зарегистрированы");
        }

        List<SubjectMarks> subjectMarks = jsonMapper.getStudentMarksBySemester(appUserRepository.findByChatId(chatId).getStudentMarks(), semesterNumber);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("*__Ваши оценки за %d семестр:__*\n\n", semesterNumber));

        if (subjectMarks.isEmpty()) {
            editMessageText.setText("У вас нет оценок за данный семестр");
        } else {
            for (SubjectMarks subjectMark : subjectMarks) {
                stringBuilder.append(String.format("*%s*:\n" +
                                                   "Атт1:  *%d*,  Атт2:  *%d*,  Атт3:  *%d*\n" +
                                                   "Итог:  *%d\\(%s\\)*",
                        subjectMark.getSubjectName(), subjectMark.getAtt1(), subjectMark.getAtt2(), subjectMark.getAtt3(), subjectMark.getResult(), subjectMark.getResult5()));
                stringBuilder.append("\n\n");
            }
            var sb = new StringBuilder(stringBuilder.toString().replace("+", "\\+").replace("-", "\\-").replace("null", "~   ~").replace(".", "\\."));
            editMessageText.setText(sb.toString());
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

    public void studentInfoProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setParseMode("MarkdownV2");

        Map<String, Object> studentInfo = apiService.getStudentInfo(chatId);

        if (studentInfo == null) {
            editMessageText.setText("Не удалось получить данные");
        } else {
            editMessageText.setText(String.format(
                    "*ФИО:*\n" + "%s %s %s\n\n" +
                    "*Факультет:*\n" + "%s\n\n" +
                    "*Специализация:*\n" + "%s\n\n" +
                    "*Направление:*\n" + "%s\n\n" +
                    "*Текущий семестр:*\n" + "%d\n\n" +
                    "*Группа:*\n" + "%d\\.%d",
                    studentInfo.get("surname"), studentInfo.get("firstname"), studentInfo.get("middlename"),
                    studentInfo.get("faculty_name"), studentInfo.get("specialization"), studentInfo.get("specialty_name"),
                    (Integer) studentInfo.get("semester"), (Integer) studentInfo.get("group"), (Integer) studentInfo.get("sub_group")));
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

    public void toggleNotificationProcess(Long chatId, Integer messageId) {
        var editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        AppUser appUser = appUserRepository.findByChatId(chatId);
        appUser.setToggleNotification(!appUser.isToggleNotification());
        appUserRepository.save(appUser);
        editMessageText.setText("Уведомления " + (appUser.isToggleNotification() ? "включены✅" : "выключены❌"));

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