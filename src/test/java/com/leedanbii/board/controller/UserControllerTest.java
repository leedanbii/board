package com.leedanbii.board.controller;

import com.leedanbii.board.dto.UserRegisterForm;
import com.leedanbii.board.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId("validUser");
        form.setUserPassword("Password1!");
        form.setUserName("John");

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .with(user("admin").roles("USER"))
                        .flashAttr("userRegisterForm", form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @ParameterizedTest
    @DisplayName("회원가입 실패 - 유효하지 않은 입력값")
    @CsvSource({
            "a, Password1!, John",
            "validUser, pass, John",
            "validUser, Password1!, "
    })
    void register_validationErrors(String userId, String userPassword, String userName) throws Exception {
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId(userId);
        form.setUserPassword(userPassword);
        form.setUserName(userName);

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .with(user("admin").roles("USER"))
                        .flashAttr("form", form))
                .andExpect(status().isOk())
                .andExpect(view().name("/register"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 중복")
    void register_duplicateUserId() throws Exception {
        UserRegisterForm form = new UserRegisterForm();
        form.setUserId("duplicateUser");
        form.setUserPassword("Password1!");
        form.setUserName("John");

        doThrow(new IllegalArgumentException("이미 존재하는 사용자 아이디입니다."))
                .when(userService).register(any(UserRegisterForm.class));

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .with(user("admin").roles("USER"))
                        .flashAttr("form", form))
                .andExpect(status().isOk())
                .andExpect(view().name("/register"))
                .andExpect(model().attributeExists("errors"));
    }
}
