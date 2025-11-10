package com.leedanbii.board.controller;

import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(UserRegisterForm form) {
        User user = new User();
        user.setUserId(form.getUserId());
        user.setPassword(form.getUserPassword());
        user.setName(form.getUserName());

        userService.register(user);

        return "redirect:/";
    }
}
