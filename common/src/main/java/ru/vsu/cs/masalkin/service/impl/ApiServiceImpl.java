package ru.vsu.cs.masalkin.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.service.ApiService;

import java.util.List;
import java.util.Map;

@Service
@Log4j
public class ApiServiceImpl implements ApiService {
    @Override
    public AppUser getUser(Long chatId, String login, String password) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = Map.of("username", login, "password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> responseTokens;
        try {
            responseTokens = restTemplate.exchange("https://www.cs.vsu.ru/brs/api/auth_jwt/login", HttpMethod.POST, request, Map.class);
        } catch (Exception e) {
            log.error(e);
            return null;
        }

        Map<String, Object> studentInfo = getStudentInfo(responseTokens.getBody().get("access_token").toString());

        AppUser appUser = new AppUser();
        appUser.setChatId(chatId);
        appUser.setFirstname((String) studentInfo.get("firstname"));
        appUser.setCurrentSemester((Integer) studentInfo.get("semester"));
        appUser.setToggleNotification(true);
        appUser.setAccessToken((String) responseTokens.getBody().get("access_token"));
        appUser.setRefreshToken((String) responseTokens.getBody().get("refresh_token"));
        appUser.setStudentMarks(getStudentMarks((String) responseTokens.getBody().get("access_token")));

        return appUser;
    }

    @Override
    public List<Map<String, Object>> getStudentMarks(String access_token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseStudentMarks = restTemplate.exchange("https://www.cs.vsu.ru/brs/api/student_marks", HttpMethod.GET, entity, Map.class);
        Map<String, Object> responseBody = responseStudentMarks.getBody();
        return new ObjectMapper().convertValue(responseBody.get("marks"), new TypeReference<>() {});
    }

    @Override
    public Map<String, Object> getStudentInfo(String access_token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseStudentInfo = restTemplate.exchange("https://www.cs.vsu.ru/brs/api/student_info", HttpMethod.GET, entity, Map.class);
        return new ObjectMapper().convertValue(responseStudentInfo.getBody(), new TypeReference<>() {});
    }
}
