package ru.vsu.cs.masalkin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Types;
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

    private String firstName;

    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean toggle_notification;

    @JdbcTypeCode(Types.LONGVARCHAR)
    private String access_token;

    @JdbcTypeCode(Types.LONGVARCHAR)
    private String refresh_token;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> student_marks;
}
