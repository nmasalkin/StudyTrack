package ru.vsu.cs.masalkin.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.vsu.cs.masalkin.api.ApiService;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.repository.AppUserRepository;
import ru.vsu.cs.masalkin.service.MainService;
import ru.vsu.cs.masalkin.service.StartService;
import ru.vsu.cs.masalkin.messaging.ProducerService;
import ru.vsu.cs.masalkin.service.enums.UserState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static ru.vsu.cs.masalkin.service.enums.ServiceCommands.*;
import static ru.vsu.cs.masalkin.service.enums.UserState.WAITING_FOR_LOGIN;
import static ru.vsu.cs.masalkin.service.enums.UserState.WAITING_FOR_PASSWORD;

@Service
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
        } else if (MENU.equals(text)) {
            if (appUserRepository.existsByChatId(update.getMessage().getChatId())) {
                mainService.menuProcess(update.getMessage().getChatId());
            } else {
                sendAnswer("⚠️ Вы не зарегистрированы. Пожалуйста, пройдите регистрацию, чтобы получать оценки и уведомления с БРС 📚", update.getMessage().getChatId());
                startProcess(update.getMessage().getChatId());
            }
        } else if (ABOUT_BOT.equals(text)) {
            mainService.aboutBotProcess(update.getMessage().getChatId());
        } else if (WAITING_FOR_LOGIN.equals(userStates.get(update.getMessage().getChatId()))) {
            handleLogin(update.getMessage().getChatId(), text);
        } else if (WAITING_FOR_PASSWORD.equals(userStates.get(update.getMessage().getChatId()))) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(update.getMessage().getChatId());
            deleteMessage.setMessageId(update.getMessage().getMessageId());
            producerService.produceDeleteMessage(deleteMessage);
            sendAnswer("📝 Провожу регистрацию... Пожалуйста, подождите. ⏳", update.getMessage().getChatId());
            handlePassword(update.getMessage().getChatId(), text);
        } else {
            sendAnswer("❌ Неизвестная команда. Пожалуйста, используйте доступные команды.", update.getMessage().getChatId());
        }
    }

    @Override
    public void processCallbackMessage(Update update) {
        var data = update.getCallbackQuery().getData();

        if (REGISTRATION.equals(data)) {
            registrationProcess(update.getCallbackQuery().getMessage().getChatId());
        } else if (MENU.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("⚠️ Вы не зарегистрированы. Пожалуйста, пройдите регистрацию, чтобы получать оценки и уведомления с БРС 📚", update.getCallbackQuery().getMessage().getChatId());
                startProcess(update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.menuProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (SEMESTER_LIST.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("⚠️ Вы не зарегистрированы. Пожалуйста, пройдите регистрацию, чтобы получать оценки и уведомления с БРС 📚", update.getCallbackQuery().getMessage().getChatId());
                startProcess(update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.chooseSemesterProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (STUDENT_INFO.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("⚠️ Вы не зарегистрированы. Пожалуйста, пройдите регистрацию, чтобы получать оценки и уведомления с БРС 📚", update.getCallbackQuery().getMessage().getChatId());
                startProcess(update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.studentInfoProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (TOGGLE_NOTIFICATION.equals(data)) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("⚠️ Вы не зарегистрированы. Пожалуйста, пройдите регистрацию, чтобы получать оценки и уведомления с БРС 📚", update.getCallbackQuery().getMessage().getChatId());
                startProcess(update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.toggleNotificationProcess(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else if (data.contains("/semester")) {
            if (!appUserRepository.existsByChatId(update.getCallbackQuery().getMessage().getChatId())) {
                sendAnswer("⚠️ Вы не зарегистрированы. Пожалуйста, пройдите регистрацию, чтобы получать оценки и уведомления с БРС 📚", update.getCallbackQuery().getMessage().getChatId());
                startProcess(update.getCallbackQuery().getMessage().getChatId());
            } else {
                mainService.semesterProcess(Integer.parseInt(data.replace("/semester_", "")), update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            }
        } else {
            sendAnswer("❌ Неизвестная команда. Пожалуйста, используйте доступные команды.", update.getCallbackQuery().getMessage().getChatId());
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
        sendMessage.setText("👋 Приветствую вас!\n" +
                            "\n" +
                            "Этот бот 🤖 создан для удобного просмотра ваших оценок и уведомлений о новых с сайта БРС 📚. Будьте в курсе всех изменений и важных событий в вашем учебном процессе! ✨\n" +
                            "\n" +
                            "Чтобы начать пользоваться ботом, пройдите быструю регистрацию 😁");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfButtons = new ArrayList<>();

        List<InlineKeyboardButton> lineOfButtons1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("📝 Регистрация");
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
        sendMessage.setText("Для регистрации необходимо ввести данные от БРС 🔐\n" +
                            "На ввод логина и пароля у вас есть 1 минута ⏳");
        producerService.produceAnswer(sendMessage);
        sendAnswer("Пожалуйста, введите ваш логин", chatId);

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            userStates.remove(chatId);
            userLogins.remove(chatId);
            pendingUsers.remove(chatId);
            sendTimeoutMessage(chatId);
        }, 1, TimeUnit.MINUTES);
        pendingUsers.put(chatId, future);

        userStates.put(chatId, WAITING_FOR_LOGIN);
    }

    public void handleLogin(Long chatId, String login) {
        userLogins.put(chatId, login);
        userStates.put(chatId, WAITING_FOR_PASSWORD);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Пожалуйста, введите ваш пароль");
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
            sendMessage.setText("Добро пожаловать, " + appUser.getFirstname()+ "\n" +
                                "✅ Регистрация успешно завершена!\n" + "\n" +
                                "Теперь вы можете просмотреть ваши текущие оценки и будете получать уведомления о новых 🔔");
            producerService.produceAnswer(sendMessage);
            mainService.menuProcess(chatId);
        } else {
            sendMessage.setText("❌ Введены неверные данные. Пожалуйста, попробуйте снова 🔄");
            producerService.produceAnswer(sendMessage);
        }

        return appUser;
    }

    private void sendTimeoutMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("⏳Время ввода истекло. Пожалуйста, попробуйте снова 🔄");
        producerService.produceAnswer(sendMessage);
        startProcess(chatId);
    }
}