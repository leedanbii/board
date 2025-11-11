package com.leedanbii.board.service;

import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.entity.Board;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.repository.BoardRepository;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board createBoard(BoardForm form, User writer) {
        Board board = new Board(form.getBoardTitle(), form.getBoardContents(), writer);
        return boardRepository.save(board);
    }
}
