package com.leedanbii.board.service;

import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.dto.BoardUpdateForm;
import com.leedanbii.board.entity.Board;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.repository.BoardRepository;
import com.leedanbii.board.util.ValidationUtils;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private static final long TITLE_LENGTH_MAX = 30;
    private static final long CONTENTS_LENGTH_MAX = 1000;

    private static final String ERROR_BOARD_NOT_FOUND = "게시글이 존재하지 않습니다.";
    private static final String ERROR_TITLE_TOO_LONG = "제목은 %d자를 초과할 수 없습니다.";
    private static final String ERROR_CONTENTS_TOO_LONG = "내용은 %d자를 초과할 수 없습니다.";
    private static final String ERROR_NO_PERMISSION = "권한이 없습니다.";

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Long createBoard(BoardForm form, User writer) {
        ValidationUtils.validateNotBlank(form.getBoardTitle(), form.getBoardContent());
        validateBoard(form);

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

    public Long updateBoard(Long boardId, BoardUpdateForm form, User loginUser) {
        ValidationUtils.validateNotBlank(form.getBoardTitle(), form.getBoardContent());
        validateUpdateBoard(form);

        Board board = findBoardAndValidatePermission(boardId, loginUser);

        board.update(form.getBoardTitle(), form.getBoardContent());
        boardRepository.save(board);

        return board.getId();
    }

    public void deleteBoard(Long boardId, User loginUser) {
        Board board = getBoard(boardId);
        validatePermission(board, loginUser);
        boardRepository.deleteById(boardId);
    }

    private void validateBoard(BoardForm form) {
        validateBoardTitle(form.getBoardTitle());
        validateBoardContent(form.getBoardContent());
    }

    private void validateUpdateBoard(BoardUpdateForm form) {
        validateBoardTitle(form.getBoardTitle());
        validateBoardContent(form.getBoardContent());
    }

    private void validateBoardTitle(String boardTitle) {
        if (boardTitle.length() > TITLE_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_TITLE_TOO_LONG, TITLE_LENGTH_MAX));
        }
    }

    private void validateBoardContent(String boardContent) {
        if (boardContent.length() > CONTENTS_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_CONTENTS_TOO_LONG, CONTENTS_LENGTH_MAX));
        }
    }

    private Board findBoardAndValidatePermission(Long boardId, User loginUser) {
        Board board = getBoard(boardId);
        validatePermission(board, loginUser);
        return board;
    }

    private void validatePermission(Board board, User loginUser) {
        if(!board.getWriter().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException(ERROR_NO_PERMISSION);
        }
    }
}
