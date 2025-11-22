package com.leedanbii.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterForm {

    private static final String ERROR_USER_ID_INVALID = "아이디는 영문으로 시작하고 영문/숫자로 구성되어야 합니다.";
    private static final String ERROR_PASSWORD_TYPE = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.";

    @NotBlank(message = "아이디를 입력하세요.")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = ERROR_USER_ID_INVALID)
    private String userId;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()])[A-Za-z\\d!@#$%^&*()]+$",
            message = ERROR_PASSWORD_TYPE
    )
    private String userPassword;

    @NotBlank(message = "이름을 입력하세요.")
    private String userName;
}
