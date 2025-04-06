package ru.vsu.cs.masalkin.api.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.api.ApiService;
import ru.vsu.cs.masalkin.repository.AppUserRepository;

import java.util.List;
import java.util.Map;

@Service
public class ApiServiceImpl implements ApiService {

    @Value("${api.auth.login}")
    private String loginUrl;
    @Value("${api.auth.refresh}")
    private String refreshUrl;
    @Value("${api.student.marks}")
    private String studentMarksUrl;
    @Value("${api.student.info}")
    private String studentInfoUrl;

    private final AppUserRepository appUserRepository;

    public ApiServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AppUser getUser(Long chatId, String login, String password) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> requestBody = Map.of("username", login, "password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> responseTokens;
        try {
            responseTokens = restTemplate.exchange(loginUrl, HttpMethod.POST, request, Map.class);
        } catch (Exception e) {
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

    public List<Map<String, Object>> getStudentMarks(String access_token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseStudentMarks = restTemplate.exchange(studentMarksUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> responseBody = responseStudentMarks.getBody();
        return new ObjectMapper().convertValue(responseBody.get("marks"), new TypeReference<>() {
        });
    }

    public Map<String, Object> getStudentInfo(String access_token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseStudentInfo = restTemplate.exchange(studentInfoUrl, HttpMethod.GET, entity, Map.class);
        return new ObjectMapper().convertValue(responseStudentInfo.getBody(), new TypeReference<>() {
        });
    }

    @Override
    public AppUser updateUser(AppUser appUser) {
        Map<String, Object> studentInfo = getStudentInfo(appUser.getChatId());
        AppUser updatedAppUser = new AppUser();
        updatedAppUser.setChatId(appUser.getChatId());
        updatedAppUser.setFirstname(appUser.getFirstname());
        updatedAppUser.setCurrentSemester((Integer) studentInfo.get("semester"));
        updatedAppUser.setToggleNotification(appUser.isToggleNotification());
        updatedAppUser.setAccessToken(appUser.getRefreshToken());
        updatedAppUser.setRefreshToken(appUser.getRefreshToken());
        updatedAppUser.setStudentMarks(getStudentMarks(appUser.getChatId()));
        return updatedAppUser;
    }

    @Override
    public List<Map<String, Object>> getStudentMarks(Long chatId) {
        AppUser appUser = appUserRepository.findByChatId(chatId);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + appUser.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseStudentMarks;
        try {
            responseStudentMarks = restTemplate.exchange(studentMarksUrl, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            String[] tokens = updateTokens(appUser.getRefreshToken());
            headers.set("Authorization", "Bearer " + tokens[0]);
            entity = new HttpEntity<>(headers);
            try {
                responseStudentMarks = restTemplate.exchange(studentMarksUrl, HttpMethod.GET, entity, Map.class);
            } catch (Exception ex) {
                return null;
            }
            appUser.setAccessToken(tokens[0]);
            appUser.setRefreshToken(tokens[1]);
            appUserRepository.save(appUser);
        }

        Map<String, Object> responseBody = responseStudentMarks.getBody();
        return new ObjectMapper().convertValue(responseBody.get("marks"), new TypeReference<>() {
        });
    }

    @Override
    public Map<String, Object> getStudentInfo(Long chatId) {
        AppUser appUser = appUserRepository.findByChatId(chatId);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + appUser.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseStudentInfo;
        try {
            responseStudentInfo = restTemplate.exchange(studentInfoUrl, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            String[] tokens = updateTokens(appUser.getRefreshToken());
            headers.set("Authorization", "Bearer " + tokens[0]);
            entity = new HttpEntity<>(headers);
            try {
                responseStudentInfo = restTemplate.exchange(studentInfoUrl, HttpMethod.GET, entity, Map.class);
            } catch (Exception ex) {
                return null;
            }
            appUser.setAccessToken(tokens[0]);
            appUser.setRefreshToken(tokens[1]);
            appUserRepository.save(appUser);
        }

        Map<String, Object> responseBody = responseStudentInfo.getBody();
        return new ObjectMapper().convertValue(responseBody, new TypeReference<>() {
        });
    }

    public String[] updateTokens(String refresh_token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + refresh_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> responseTokens;
        try {
            responseTokens = restTemplate.exchange(refreshUrl, HttpMethod.POST, entity, Map.class);
        } catch (Exception e) {
            return null;
        }
        String[] tokens = new String[2];
        tokens[0] = (String) responseTokens.getBody().get("access_token");
        tokens[1] = (String) responseTokens.getBody().get("refresh_token");
        return tokens;
    }
}
