package com.leedanbii.board.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.leedanbii.board.domain.User;
import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private static final String VALID_USER_ID = "danbi1";
    private static final String VALID_PASSWORD = "Password1!";
    private static final String VALID_NAME = "단비";

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId(VALID_USER_ID);
        form.setUserPassword(VALID_PASSWORD);
        form.setUserName(VALID_NAME);

        when(userRepository.findByUserId(VALID_USER_ID)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(VALID_PASSWORD);

        assertThatCode(() -> userService.register(form))
                .doesNotThrowAnyException();

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디")
    void register_duplicateUserId_fail() {
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId(VALID_USER_ID);
        form.setUserPassword(VALID_PASSWORD);
        form.setUserName(VALID_NAME);

        when(userRepository.findByUserId(VALID_USER_ID)).thenReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 사용자 아이디입니다.");
    }
}
