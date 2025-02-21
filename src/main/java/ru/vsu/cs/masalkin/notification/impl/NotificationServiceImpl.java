package ru.vsu.cs.masalkin.notification.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.entity.SubjectMarks;
import ru.vsu.cs.masalkin.repository.AppUserRepository;
import ru.vsu.cs.masalkin.api.ApiService;
import ru.vsu.cs.masalkin.notification.NotificationService;
import ru.vsu.cs.masalkin.messaging.ProducerService;
import ru.vsu.cs.masalkin.service.impl.JsonMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j
public class NotificationServiceImpl implements NotificationService {

    private final AppUserRepository appUserRepository;
    private final ApiService apiService;
    private final JsonMapper jsonMapper;
    private final ProducerService producerService;

    public NotificationServiceImpl(AppUserRepository appUserRepository, ApiService apiService, JsonMapper jsonMapper, ProducerService producerService) {
        this.appUserRepository = appUserRepository;
        this.apiService = apiService;
        this.jsonMapper = jsonMapper;
        this.producerService = producerService;
    }

    @Override
    @Scheduled(fixedRate = 180000)
    public void notificationProcess() {
        log.debug("Start notification process");
        List<AppUser> appUserList = appUserRepository.findAppUsersByToggleNotificationIsTrue();
        for (AppUser appUser : appUserList) {
            List<SubjectMarks> newMarks = jsonMapper.getStudentMarks(apiService.getStudentMarks(appUser.getChatId()));
            List<SubjectMarks> appUserMarks = jsonMapper.getStudentMarks(appUser.getStudentMarks());
            if (!newMarks.equals(appUserMarks)) {
                SendMessage sendMessage = new SendMessage();
                if (newMarks.size() == appUserMarks.size()) {
                    sendMessage.setChatId(appUser.getChatId());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Получена/изменена оценка по предметам:").append("\n");
                    for (int i = 0; i < newMarks.size(); i++) {
                        if (!newMarks.get(i).equals(appUserMarks.get(i))){
                            stringBuilder.append(newMarks.get(i).getSubjectName()).append("\n");
                        }
                    }
                    sendMessage.setText(stringBuilder.toString());
                } else {
                    sendMessage.setChatId(appUser.getChatId());
                    sendMessage.setText("Доступен новый семестр/предмет");
                }
                AppUser updateAppUser = apiService.updateUser(appUser);
                appUserRepository.save(updateAppUser);
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();
                List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
                InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
                inlineKeyboardButton1.setText("Список семестров");
                inlineKeyboardButton1.setCallbackData("/semester_list");
                lineOfButtons1.add(inlineKeyboardButton1);
                listOfButtons.add(lineOfButtons1);
                markupInline.setKeyboard(listOfButtons);
                sendMessage.setReplyMarkup(markupInline);
                producerService.produceAnswer(sendMessage);
            }
        }
    }
}
