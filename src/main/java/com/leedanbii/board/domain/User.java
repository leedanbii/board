package com.leedanbii.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.function.Function;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    private static final int NAME_LENGTH_MAX = 5;

    public static final int USER_ID_MIN_LENGTH = 5;
    public static final int USER_ID_MAX_LENGTH = 20;

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 30;

    private static final String USER_ID_PATTERN = "^[a-zA-Z][a-zA-Z0-9]*$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]+$";

    private static final String ERROR_NAME_TOO_LONG = "이름은 %d자 이하만 가능합니다.";
    private static final String ERROR_USER_ID_INVALID = "아이디는 %d~%d자여야 합니다.";
    private static final String ERROR_PASSWORD_INVALID = "비밀번호는 %d~%d자여야 합니다.";
    private static final String ERROR_USER_ID_PATTERN = "아이디 형식이 올바르지 않습니다.";
    private static final String ERROR_PASSWORD_PATTERN = "비밀번호 형식이 올바르지 않습니다.";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 5)
    private String name;

    private User(String userId, String rawPassword, String name, Function<String, String> encoder) {
        userId = userId.trim();
        rawPassword = rawPassword.trim();
        name = name.trim();
        validateInput(userId, rawPassword, name);
        this.userId = userId;
        this.password = encoder.apply(rawPassword);
        this.name = name;
    }

    public static User of(String userId, String rawPassword, String name, Function<String, String> encoder) {
        return new User(userId, rawPassword, name, encoder);
    }

    private void validateInput(String userId, String password, String name) {
        validateUserId(userId);
        validatePassword(password);
        validateName(name);
    }

    private void validateName(String name) {
        if (name.isBlank() || name.length() > NAME_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_NAME_TOO_LONG, NAME_LENGTH_MAX));
        }
    }

   private void validateUserId(String userId) {
        if (userId.isBlank() || userId.length() < USER_ID_MIN_LENGTH || userId.length() > USER_ID_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format(ERROR_USER_ID_INVALID, USER_ID_MIN_LENGTH, USER_ID_MAX_LENGTH));
        }

        if (!userId.matches(USER_ID_PATTERN)) {
            throw new IllegalArgumentException(ERROR_USER_ID_PATTERN);
        }
    }

    private void validatePassword(String password) {
        if (password.isBlank() || password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format(ERROR_PASSWORD_INVALID, PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH));
        }

        if (!password.matches(PASSWORD_PATTERN)) {
            throw new IllegalArgumentException(ERROR_PASSWORD_PATTERN);
        }
    }
}
