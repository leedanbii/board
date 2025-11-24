package com.leedanbii.board.service;

import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String ERROR_DUPLICATE_USER_ID = "이미 존재하는 사용자 아이디입니다.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(UserRegisterForm form) {
        checkDuplicateUserId(form.getUserId());

        User user = User.of(form.getUserId(), form.getUserPassword(), form.getUserName(), passwordEncoder::encode);
        userRepository.save(user);
    }

    private void checkDuplicateUserId(String userId) {
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException(ERROR_DUPLICATE_USER_ID);
        }
    }
}
