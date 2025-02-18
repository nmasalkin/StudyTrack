package ru.vsu.cs.masalkin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(exclude = "chatId")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    private Long chatId;

    private String firstname;

    private Integer currentSemester;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean toggleNotification;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String accessToken;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String refreshToken;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> studentMarks;
}
