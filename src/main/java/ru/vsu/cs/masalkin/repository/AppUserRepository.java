package ru.vsu.cs.masalkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.masalkin.entity.AppUser;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByChatId(Long chatId);
    AppUser findByChatId(Long chatId);
    List<AppUser> findAppUsersByToggleNotificationIsTrue();
}