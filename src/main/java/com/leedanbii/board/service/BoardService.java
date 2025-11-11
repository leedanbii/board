package com.leedanbii.board.service;

import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.entity.Board;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.repository.BoardRepository;
import java.util.List;
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

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board getBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
    }
}
