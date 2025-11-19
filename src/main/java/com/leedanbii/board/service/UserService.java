package com.leedanbii.board.service;

import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.repository.UserRepository;
import com.leedanbii.board.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int NAME_LENGTH_MAX = 5;
    private static final String ERROR_NAME_TOO_LONG = "이름은 %d자 이하만 가능합니다.";
    private static final String ERROR_DUPLICATE_USER_ID = "이미 존재하는 사용자 아이디입니다.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(UserRegisterForm form) {
        ValidationUtils.validateNotBlank(form.getUserId(), form.getUserPassword(), form.getUserName());
        validateUserForm(form);
        checkDuplicateUserId(form.getUserId());

        String encodedPassword = encodePassword(form.getUserPassword());

        User user = User.of(form.getUserId(), encodedPassword, form.getUserName());
        return userRepository.save(user);
    }

    private void validateUserForm(UserRegisterForm form) {
        if (form.getUserName().length() > NAME_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_NAME_TOO_LONG, NAME_LENGTH_MAX));
        }
    }

    private void checkDuplicateUserId(String userId) {
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException(ERROR_DUPLICATE_USER_ID);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
