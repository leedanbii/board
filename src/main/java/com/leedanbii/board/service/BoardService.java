package com.leedanbii.board.service;

import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.dto.BoardUpdateForm;
import com.leedanbii.board.domain.Board;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.repository.BoardRepository;
import com.leedanbii.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private static final String ERROR_BOARD_NOT_FOUND = "게시글이 존재하지 않습니다.";
    private static final String ERROR_USER_NOT_FOUND = "사용자가 존재하지 않습니다.";
    private static final String ERROR_NO_PERMISSION = "권한이 없습니다.";

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createBoard(BoardForm form, String userId) {
        User writer = getUserByUserId(userId);
        Board board = Board.of(form.getBoardTitle(), form.getBoardContent(), writer);
        boardRepository.save(board);
        return board.getId();
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board getBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_BOARD_NOT_FOUND));
    }

    @Transactional
    public Long updateBoard(Long boardId, BoardUpdateForm form, String userId) {
        Board board = findBoardAndValidatePermission(boardId, userId);
        board.update(form.getBoardTitle(), form.getBoardContent());
        boardRepository.save(board);
        return board.getId();
    }

    public Board getBoardForUpdate(Long id, String userId) {
        if(!findWriterUserIdByBoardId(id).equals(userId)) {
            throw new IllegalArgumentException(ERROR_NO_PERMISSION);
        }
        return getBoard(id);
    }

    @Transactional
    public void deleteBoard(Long boardId, String userId) {
        validatePermission(boardId, userId);
        boardRepository.deleteById(boardId);
    }

    public User getUserByUserId(String id) {
        return userRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
    }

    private Board findBoardAndValidatePermission(Long boardId, String userId) {
        validatePermission(boardId, userId);
        return getBoard(boardId);
    }

    private void validatePermission(Long boardId, String userId) {
        String writerUserId = findWriterUserIdByBoardId(boardId);
        if(!Objects.equals(writerUserId, userId)) {
            throw new IllegalArgumentException(ERROR_NO_PERMISSION);
        }
    }

    private String findWriterUserIdByBoardId(Long id) {
        return getBoard(id).getWriter().getUserId();
    }
}
