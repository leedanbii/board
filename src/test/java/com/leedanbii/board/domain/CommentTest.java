package com.leedanbii.board.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommentTest {

    private User user;
    private Board board;

    private static final int COMMENT_MAX_LENGTH = 300;

    @BeforeEach
    void setUp() {
        user = User.of("lee123", "Password1!", "단비");
        board = Board.of("제목", "내용", user);
    }

    @Test
    @DisplayName("정상적인 댓글 생성")
    void createComment_success() {
        assertThatCode(() -> Comment.of("댓글 내용", board, user))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("댓글 내용은 앞뒤 공백 제거 후 저장")
    void createComment_trimContent() {
        Comment comment = Comment.of("  공백 포함 댓글  ", board, user);

        assertEquals("공백 포함 댓글", comment.getContent());
    }

    @Test
    @DisplayName("댓글 내용이 빈 문자열이면 예외 발생")
    void createComment_blankContent_fail() {
        assertThatThrownBy(() -> Comment.of("   ", board, user))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("댓글 내용이 최대 길이 초과 시 예외 발생")
    void createComment_tooLongContent_fail() {
        String longContent = "a".repeat(COMMENT_MAX_LENGTH + 1);

        assertThatThrownBy(() -> Comment.of(longContent, board, user))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
