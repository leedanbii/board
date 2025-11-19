package com.leedanbii.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private static final String ERROR_NAME_TOO_LONG = "이름은 %d자 이하만 가능합니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private User(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public static User of(String userId, String password, String name) {
        validateName(name);
        return new User(userId, password, name);
    }

    public static void validateName(String name) {
        if (name.length() > NAME_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_NAME_TOO_LONG, NAME_LENGTH_MAX));
        }
    }
}
