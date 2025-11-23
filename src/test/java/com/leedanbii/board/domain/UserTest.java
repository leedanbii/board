package com.leedanbii.board.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    @DisplayName("정상적으로 User 생성")
    void createUser_success() {
        assertThatCode(() -> User.of("danbi1", "Password1!", "단비"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이름이 길이 제한 초과하면 예외 발생")
    void createUser_nameTooLong_throwsException() {
        assertThatThrownBy(() ->
                User.of("danbi1", "Password1!", "이름초과테스트용")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("아이디 길이가 최소 미만이면 예외 발생")
    void createUser_userIdTooShort_throwsException() {
        assertThatThrownBy(() ->
                User.of("a", "Password1!", "단비")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("아이디 길이가 최대 초과하면 예외 발생")
    void createUser_userIdTooLong_throwsException() {
        String longId = "a".repeat(21);

        assertThatThrownBy(() ->
                User.of(longId, "Password1!", "단비")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("아이디 패턴에 맞지 않으면 예외 발생")
    void createUser_userIdPatternInvalid_throwsException() {
        assertThatThrownBy(() ->
                User.of("1invalid", "Password1!", "단비")
        ).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->
                User.of("inva#lid$", "Password1!", "단비")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비밀번호 길이가 짧으면 예외 발생")
    void createUser_passwordTooShort_throwsException() {
        assertThatThrownBy(() ->
                User.of("danbi1", "Ab1!", "단비")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비밀번호 패턴이 맞지 않으면 예외 발생")
    void createUser_passwordPatternInvalid_throwsException() {
        // 숫자 없음
        assertThatThrownBy(() ->
                User.of("danbi1", "Abcdefgh!", "단비")
        ).isInstanceOf(IllegalArgumentException.class);

        // 특수문자 없음
        assertThatThrownBy(() ->
                User.of("danbi1", "Abcd1234", "단비")
        ).isInstanceOf(IllegalArgumentException.class);

        // 알파벳 없음
        assertThatThrownBy(() ->
                User.of("danbi1", "1234!@#$", "단비")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
