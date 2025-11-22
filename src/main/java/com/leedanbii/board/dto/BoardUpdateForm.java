package com.leedanbii.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateForm {
    @NotBlank(message = "제목을 입력하세요.")
    private String boardTitle;
    @NotBlank(message = "내용을 입력하세요.")
    private String boardContent;
}
