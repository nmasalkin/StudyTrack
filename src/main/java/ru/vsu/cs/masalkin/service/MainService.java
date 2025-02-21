package ru.vsu.cs.masalkin.service;

public interface MainService {
    void menuProcess(Long chatId);
    void menuProcess(Long chatId, Integer messageId);
    void chooseSemesterProcess(Long chatId, Integer messageId);
    void semesterProcess(int semesterNumber, Long chatId, Integer messageId);
    void studentInfoProcess(Long chatId, Integer messageId);
    void toggleNotificationProcess(Long chatId, Integer messageId);
    void aboutBotProcess(Long chatId);
}