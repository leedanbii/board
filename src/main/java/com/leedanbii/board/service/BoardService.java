package com.leedanbii.board.service;

import com.leedanbii.board.dto.BoardForm;
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

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Long createBoard(BoardForm form, User writer) {
        ValidationUtils.validateNotBlank(form.getBoardTitle(), form.getBoardContents());
        validateBoard(form);

        Board board = Board.of(form.getBoardTitle(), form.getBoardContents(), writer);
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

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    private void validateBoard(BoardForm form) {
        validateBoardTitle(form.getBoardTitle());
        validateBoardContents(form.getBoardContents());
    }

    private void validateBoardTitle(String boardTitle) {
        if (boardTitle.length() > TITLE_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_TITLE_TOO_LONG, TITLE_LENGTH_MAX));
        }
    }

    private void validateBoardContents(String boardContents) {
        if (boardContents.length() > CONTENTS_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_CONTENTS_TOO_LONG, CONTENTS_LENGTH_MAX));
        }
    }
}
