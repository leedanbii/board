package com.leedanbii.board.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.leedanbii.board.domain.Board;
import com.leedanbii.board.domain.Comment;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.dto.CommentForm;
import com.leedanbii.board.repository.BoardRepository;
import com.leedanbii.board.repository.CommentRepository;
import com.leedanbii.board.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        testUser = User.of("lee123", "Password1!", "단비");
        testBoard = Board.of("제목", "내용", testUser);
        ReflectionTestUtils.setField(testBoard, "id", 1L);
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_success() {
        CommentForm form = new CommentForm();
        form.setContent("댓글 내용");

        given(boardRepository.findById(1L)).willReturn(Optional.of(testBoard));
        given(userRepository.findByUserId("lee123")).willReturn(Optional.of(testUser));

        assertThatCode(() -> commentService.createComment(form, "lee123", 1L))
                .doesNotThrowAnyException();

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success() {
        Comment comment = Comment.of("댓글 내용", testBoard, testUser);
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        assertThatCode(() -> commentService.deleteComment(1L, "lee123"))
                .doesNotThrowAnyException();

        verify(commentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("댓글 생성 실패 - 유저 없음")
    void createComment_userNotFound() {
        CommentForm form = new CommentForm();
        form.setContent("댓글 내용");

        given(boardRepository.findById(1L)).willReturn(Optional.of(testBoard));
        given(userRepository.findByUserId("lee123")).willReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(form, "lee123", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 생성 실패 - 게시글 없음")
    void createComment_boardNotFound() {
        CommentForm form = new CommentForm();
        form.setContent("댓글 내용");

        given(boardRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(form, "lee123", 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 조회 실패 - 존재하지 않음")
    void getComment_notFound() {
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getComment(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void deleteComment_noPermission() {
        User otherUser = User.of("other123", "Password1!", "철수");
        Comment comment = Comment.of("댓글 내용", testBoard, testUser);
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(1L, otherUser.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }
}
