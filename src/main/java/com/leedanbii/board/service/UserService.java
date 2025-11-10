package com.leedanbii.board.service;

import com.leedanbii.board.entity.User;
import com.leedanbii.board.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (userRepository.findByUserId(user.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        Optional.of(user)
                .filter(u -> u.getName().length() <= 5)
                .orElseThrow(() -> new IllegalArgumentException("이름은 5자 이하만 가능합니다."));
        return userRepository.save(user);
    }

    public User login(UserLoginForm form) {
        String userId = form.getUserId();
        String password = form.getUserPassword();
        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("아이디와 비밀번호를 모두 입력해야 합니다.");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user;
    }
}
