package com.leedanbii.board.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private User testUser() {
        return User.of("testUser", "Abcd1234!", "단비");
    }

    @Test
    @DisplayName("정상적으로 Board 생성")
    void createBoard_success() {
        User writer = testUser();

        assertThatCode(() -> Board.of("제목", "내용", writer))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("제목이 비어있으면 예외 발생")
    void createBoard_titleBlank_throwsException() {
        User writer = testUser();

        assertThatThrownBy(() ->
                Board.of("   ", "내용", writer)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("제목이 길이 제한(30자) 초과하면 예외 발생")
    void createBoard_titleTooLong_throwsException() {
        User writer = testUser();
        String longTitle = "a".repeat(31);

        assertThatThrownBy(() ->
                Board.of(longTitle, "내용", writer)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("내용이 비어있으면 예외 발생")
    void createBoard_contentBlank_throwsException() {
        User writer = testUser();

        assertThatThrownBy(() ->
                Board.of("제목", "   ", writer)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("내용이 길이 제한(1000자) 초과하면 예외 발생")
    void createBoard_contentTooLong_throwsException() {
        User writer = testUser();
        String longContent = "a".repeat(1001);

        assertThatThrownBy(() ->
                Board.of("제목", longContent, writer)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("update() 정상 동작")
    void updateBoard_success() {
        User writer = testUser();
        Board board = Board.of("제목", "내용", writer);

        board.update("새 제목", "새 내용");

        assertThat(board.getTitle()).isEqualTo("새 제목");
        assertThat(board.getContent()).isEqualTo("새 내용");
    }

    @Test
    @DisplayName("update() 시 잘못된 입력이면 예외 발생")
    void updateBoard_invalidInput_throwsException() {
        User writer = testUser();
        Board board = Board.of("제목", "내용", writer);

        assertThatThrownBy(() ->
                board.update("", "내용")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
