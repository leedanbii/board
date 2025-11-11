package com.leedanbii.board.service;

import com.leedanbii.board.dto.UserLoginForm;
import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(UserRegisterForm form) {
        validateUserForm(form);
        checkDuplicateUserId(form.getUserId());

        User user = new User(form.getUserId(), form.getUserPassword(), form.getUserName());
        return userRepository.save(user);
    }

    public User login(UserLoginForm form) {
        validateLoginForm(form);
        User user = findUserById(form.getUserId());
        validatePassword(user, form.getUserPassword());

        return user;
    }

    private void validateUserForm(UserRegisterForm form) {
        validateNotBlank(form.getUserId(), form.getUserPassword(), form.getUserName());

        if (form.getUserName().length() > 5) {
            throw new IllegalArgumentException("이름은 5자 이하만 가능합니다.");
        }
    }

    private void checkDuplicateUserId(String userId) {
        if (userRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 아이디입니다.");
        }
    }

    private void validateLoginForm(UserLoginForm form) {
        validateNotBlank(form.getUserId(), form.getUserPassword());
    }

    private User findUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
    }

    private void validatePassword(User user, String password) {
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
    }

    private void validateNotBlank(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("모든 항목을 입력해야 합니다.");
            }
        }
    }
}
