package ru.vsu.cs.masalkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vsu.cs.masalkin.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByChatId(Long chatId);
    AppUser findByChatId(Long chatId);
}
