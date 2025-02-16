package ru.vsu.cs.masalkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.vsu.cs.masalkin.entity.AppUser;
import ru.vsu.cs.masalkin.entity.SubjectMarks;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByChatId(Long chatId);
    AppUser findByChatId(Long chatId);
}
