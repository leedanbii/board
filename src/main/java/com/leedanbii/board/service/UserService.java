package com.leedanbii.board.service;

import com.leedanbii.board.dto.UserLoginForm;
import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.repository.UserRepository;
import com.leedanbii.board.util.ValidationUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final int NAME_LENGTH_MAX = 5;
    private static final String ERROR_NAME_TOO_LONG = "이름은 %d자 이하만 가능합니다.";
    private static final String ERROR_DUPLICATE_USER_ID = "이미 존재하는 사용자 아이디입니다.";
    private static final String ERROR_USER_NOT_FOUND = "존재하지 않는 아이디입니다.";
    private static final String ERROR_INVALID_PASSWORD = "비밀번호가 올바르지 않습니다.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserRegisterForm form) {
        ValidationUtils.validateNotBlank(form.getUserId(), form.getUserPassword(), form.getUserName());
        validateUserForm(form);
        checkDuplicateUserId(form.getUserId());

        String encodedPassword = encodePassword(form.getUserPassword());

        User user = User.of(form.getUserId(), encodedPassword, form.getUserName());
        return userRepository.save(user);
    }

    public User login(UserLoginForm form) {
        validateLoginForm(form);
        User user = findUserById(form.getUserId());
        validatePassword(user, form.getUserPassword());

        return user;
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

    public void validateLoginForm(UserLoginForm form) {
        ValidationUtils.validateNotBlank(form.getUserId(), form.getUserPassword());
    }

    private User findUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
    }

    private void validatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(ERROR_INVALID_PASSWORD);
        }
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
