package ru.vsu.cs.masalkin.api;

import ru.vsu.cs.masalkin.entity.AppUser;

import java.util.List;
import java.util.Map;

public interface ApiService {
    AppUser getUser(Long chatId, String login, String password);
    AppUser updateUser(AppUser appUser);
    List<Map<String, Object>> getStudentMarks(Long chatId);
    Map<String, Object> getStudentInfo(Long chatId);
}
