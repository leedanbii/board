package com.leedanbii.board.controller;

import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.service.UserService;
import com.leedanbii.board.util.ValidationUtils;
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
        ValidationUtils.validateNotBlank(form.getUserId(), form.getUserPassword(), form.getUserName());
        userService.register(form);
        return "redirect:/";
    }
}
