package ru.vsu.cs.masalkin.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.repository.AppUserRepository;
import ru.vsu.cs.masalkin.service.ApiService;
import ru.vsu.cs.masalkin.service.MainService;
import ru.vsu.cs.masalkin.service.StartService;
import ru.vsu.cs.masalkin.service.ProducerService;
import ru.vsu.cs.masalkin.service.enums.UserState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static ru.vsu.cs.masalkin.service.enums.ServiceCommands.*;
import static ru.vsu.cs.masalkin.service.enums.UserState.WAITING_FOR_LOGIN;
import static ru.vsu.cs.masalkin.service.enums.UserState.WAITING_FOR_PASSWORD;

@Service
@Log4j
public class StartServiceImpl implements StartService {

    private final ProducerService producerService;
    private final MainService mainService;
    private final AppUserRepository appUserRepository;
    private final ApiService apiService;

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<Long, String> userLogins = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> pendingUsers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public StartServiceImpl(ProducerService producerService, MainService mainService, AppUserRepository appUserRepository, ApiService apiService) {
        this.producerService = producerService;
        this.mainService = mainService;
        this.appUserRepository = appUserRepository;
        this.apiService = apiService;
    }

    public void processTextMessage(Update update) {
        var text = update.getMessage().getText();

        if (START.equals(text)) {
            startProcess(update.getMessage().getChatId());
            log.debug("Start command received " + update);
        } else if (MENU.equals(text)) {
            if (appUserRepository.existsByChatId(update.getMessage().getChatId())) {
                mainService.menuProcess(update.getMessage().getChatId());
                log.debug("Menu command received " + update);
            } else {
                sendAnswer("Вы не зарегистрированы", update.getMessage().getChatId());
                startProcess(update.getMessage().getChatId());
                log.debug("User not registered " + update);
            }
        } else if (ABOUT_BOT.equals(text)) {
            mainService.aboutBotProcess(update.getMessage().getChatId());
            log.debug("About bot command received " + update);
        } else if (WAITING_FOR_LOGIN.equals(userStates.get(update.getMessage().getChatId()))) {
            handleLogin(update.getMessage().getChatId(), text);
            log.debug("The login is introduced " + update);
        } else if (WAITING_FOR_PASSWORD.equals(userStates.get(update.getMessage().getChatId()))) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(update.getMessage().getChatId());
            deleteMessage.setMessageId(update.getMessage().getMessageId());
            producerService.produceDeleteMessage(deleteMessage);
            sendAnswer("Провожу регистрацию...", update.getMessage().getChatId());
            handlePassword(update.getMessage().getChatId(), text);
            log.debug("The password is introduced " + update);
        } else {
            log.error("Unknown command " + text);
            sendAnswer("Неизвестная команда", update.getMessage().getChatId());
        }
    }

    @Override
    public void processCallbackMessage(Update update) {
        var data = update.getCallbackQuery().getData();

        if (REGISTRATION.equals(data)) {
            registrationProcess(update.getCallbackQuery().getMessage().getChatId());
        } else if (MENU.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("Вы не зарегистрированы", update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.menuProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (SEMESTER_LIST.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("Вы не зарегистрированы", update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.chooseSemesterProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (STUDENT_INFO.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("Вы не зарегистрированы", update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.studentInfoProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (TOGGLE_NOTIFICATION.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("Вы не зарегистрированы", update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.toggleNotificationProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (data.contains("/semester")) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("Вы не зарегистрированы", update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.semesterProcess(Integer.parseInt(data.replace("/semester_", "")), update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
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

        listOfButtons.add(lineOfButtons1);

        markupInline.setKeyboard(listOfButtons);
        sendMessage.setReplyMarkup(markupInline);

        producerService.produceAnswer(sendMessage);
    }

    private void registrationProcess(Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Для того чтобы зарегистрироваться нужно ввести данные от БРС.\n" +
                            "На ввод логина и пароля дается 2 минуты.");
        producerService.produceAnswer(sendMessage);
        sendAnswer("Введите логин", chatId);

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            userStates.remove(chatId);
            userLogins.remove(chatId);
            pendingUsers.remove(chatId);
            sendTimeoutMessage(chatId);
        }, 2, TimeUnit.MINUTES);
        pendingUsers.put(chatId, future);

        userStates.put(chatId, WAITING_FOR_LOGIN);
    }

    public void handleLogin(Long chatId, String login) {
        userLogins.put(chatId, login);
        userStates.put(chatId, WAITING_FOR_PASSWORD);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Введите пароль");
        producerService.produceAnswer(sendMessage);
    }

    public void handlePassword(Long chatId, String password) {
        saveUser(chatId, userLogins.get(chatId), password);
        userStates.remove(chatId);
        userLogins.remove(chatId);
        ScheduledFuture<?> future = pendingUsers.remove(chatId);
        if (future != null) {
            future.cancel(false);
        }
    }

    private AppUser saveUser(Long chatId, String login, String password) {
        AppUser appUser = apiService.getUser(chatId, login, password);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (appUser != null) {
            appUserRepository.save(appUser);
            sendMessage.setText(appUser.getFirstname() + ", вы успешно зарегистрировались!");
            producerService.produceAnswer(sendMessage);
            mainService.menuProcess(chatId);
        } else {
            sendMessage.setText("Неверные данные");
            producerService.produceAnswer(sendMessage);
        }

        return appUser;
    }

    private void sendTimeoutMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("⏳Время для ввода истекло. Повторите попытку.");
        producerService.produceAnswer(sendMessage);
        startProcess(chatId);
    }
}
