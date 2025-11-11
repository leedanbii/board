package com.leedanbii.board.controller;

import com.leedanbii.board.dto.UserLoginForm;
import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.service.UserService;
import jakarta.servlet.http.HttpSession;
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
        userService.register(form);
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(UserLoginForm form, HttpSession session) {
        User loggedIn = userService.login(form);

        session.setAttribute("loginUser", loggedIn);

        return "redirect:/boards";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
