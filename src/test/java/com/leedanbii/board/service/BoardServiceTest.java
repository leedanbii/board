package com.leedanbii.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.leedanbii.board.domain.Board;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.dto.BoardDetailResponse;
import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.dto.BoardResponse;
import com.leedanbii.board.dto.BoardUpdateForm;
import com.leedanbii.board.repository.BoardRepository;
import com.leedanbii.board.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BoardService boardService;

    private User testUser;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        testUser = User.of("lee123", "Password1!", "단비");
        testBoard = Board.of("제목", "내용", testUser);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createBoard_success() {
        BoardForm form = new BoardForm();
        form.setBoardTitle("제목");
        form.setBoardContent("내용");

        when(userRepository.findByUserId("lee123")).thenReturn(Optional.of(testUser));
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        Long boardId = boardService.createBoard(form, "lee123");

        assertThat(boardId).isEqualTo(testBoard.getId());
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글 생성 실패 - 사용자 없음")
    void createBoard_userNotFound() {
        BoardForm form = new BoardForm();
        form.setBoardTitle("제목");
        form.setBoardContent("내용");

        when(userRepository.findByUserId("lee123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.createBoard(form, "lee123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteBoard_success() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        boardService.deleteBoard(1L, "lee123");

        verify(boardRepository).deleteById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 권한 없음")
    void deleteBoard_noPermission() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(testBoard));

        assertThatThrownBy(() -> boardService.deleteBoard(1L, "otherUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void getBoardDetail_success() {
        when(boardRepository.findByIdWithComments(1L)).thenReturn(Optional.of(testBoard));

        BoardDetailResponse response = boardService.getBoardDetail(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testBoard.getTitle());
    }

    @Test
    @DisplayName("게시글 조회 실패 - 존재하지 않음")
    void getBoardDetail_notFound() {
        when(boardRepository.findByIdWithComments(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.getBoardDetail(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시글이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateBoard_success() {
        BoardUpdateForm updateForm = new BoardUpdateForm();
        updateForm.setBoardTitle("수정된 제목");
        updateForm.setBoardContent("수정된 내용");

        given(boardRepository.findById(testBoard.getId())).willReturn(Optional.of(testBoard));
        given(boardRepository.save(any(Board.class))).willReturn(testBoard);

        Long updatedId = boardService.updateBoard(testBoard.getId(), updateForm, testUser.getUserId());

        assertThat(updatedId).isEqualTo(testBoard.getId());
        assertThat(testBoard.getTitle()).isEqualTo("수정된 제목");
        assertThat(testBoard.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 게시글 없음")
    void updateBoard_fail_boardNotFound() {
        BoardUpdateForm updateForm = new BoardUpdateForm();
        updateForm.setBoardTitle("수정된 제목");
        updateForm.setBoardContent("수정된 내용");
        Long nonExistentId = 999L;

        given(boardRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.updateBoard(nonExistentId, updateForm, testUser.getUserId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 권한 없음")
    void updateBoard_fail_noPermission() {
        BoardUpdateForm updateForm = new BoardUpdateForm();
        updateForm.setBoardTitle("수정된 제목");
        updateForm.setBoardContent("수정된 내용");
        User otherUser = User.of("other123", "Password1!", "철수");

        given(boardRepository.findById(testBoard.getId())).willReturn(Optional.of(testBoard));

        assertThatThrownBy(() -> boardService.updateBoard(testBoard.getId(), updateForm, otherUser.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("게시글 수정 화면 조회 실패 - 권한 없음")
    void getBoardForUpdate_noPermission() {
        given(boardRepository.findById(testBoard.getId())).willReturn(Optional.of(testBoard));

        assertThatThrownBy(() -> boardService.getBoardForUpdate(testBoard.getId(), "otherUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("모든 게시글 조회 성공")
    void getAllBoards_success() {
        List<Board> boards = List.of(testBoard);
        given(boardRepository.findAllWithWriter()).willReturn(boards);

        List<BoardResponse> responses = boardService.getAllBoards();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo(testBoard.getTitle());
    }
}
