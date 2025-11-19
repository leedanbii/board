package com.leedanbii.board.controller;

import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public String register(UserRegisterForm form) {
        userService.register(form);
        return "redirect:/";
    }
}
