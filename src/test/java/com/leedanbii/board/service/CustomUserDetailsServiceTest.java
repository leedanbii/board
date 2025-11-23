package com.leedanbii.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.leedanbii.board.domain.User;
import com.leedanbii.board.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private static final String VALID_USER_ID = "danbi1";
    private static final String VALID_PASSWORD = "Password1!";
    private static final String VALID_NAME = "단비";

    @Test
    @DisplayName("사용자 조회 성공")
    void loadUserByUsername_success() {
        User user = User.of(VALID_USER_ID, VALID_PASSWORD, VALID_NAME);

        given(userRepository.findByUserId(VALID_USER_ID)).willReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(VALID_USER_ID);

        assertThat(userDetails.getUsername()).isEqualTo(VALID_USER_ID);
        assertThat(userDetails.getPassword()).isEqualTo(VALID_PASSWORD);
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("USER");
    }

    @Test
    @DisplayName("사용자 조회 실패 - 존재하지 않음")
    void loadUserByUsername_notFound() {
        given(userRepository.findByUserId("unknown")).willReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
