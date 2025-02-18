package ru.vsu.cs.masalkin.service;

import ru.vsu.cs.masalkin.entity.AppUser;

import java.util.List;
import java.util.Map;

public interface ApiService {
    AppUser getUser(Long chatId, String login, String password);
    List<Map<String, Object>> getStudentMarks(String access_token);
    Map<String, Object> getStudentInfo(String access_token);
}
