package com.leedanbii.board.service;

import com.leedanbii.board.domain.Board;
import com.leedanbii.board.domain.Comment;
import com.leedanbii.board.domain.User;
import com.leedanbii.board.dto.CommentForm;
import com.leedanbii.board.repository.BoardRepository;
import com.leedanbii.board.repository.CommentRepository;
import com.leedanbii.board.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String ERROR_COMMENT_NOT_FOUND = "댓글을 찾을 수 없습니다.";
    private static final String ERROR_NO_PERMISSION = "권한이 없습니다.";
    private static final String ERROR_USER_NOT_FOUND = "유저를 찾을 수 없습니다.";
    private static final String ERROR_BOARD_NOT_FOUND = "게시글을 찾을 수 없습니다.";

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void createComment(CommentForm form, String commenterId, Long boardId) {
        Comment comment = Comment.of(form.getContent(), getBoardByBoardId(boardId), getUserByUserId(commenterId));
        commentRepository.save(comment);
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_COMMENT_NOT_FOUND));
    }

    @Transactional
    public void deleteComment(Long commentId, String loginUserId) {
        validatePermission(commentId, loginUserId);
        commentRepository.deleteById(commentId);
    }

    private void validatePermission(Long commentId, String userId) {
        String commenterUserId = findCommenterIdByCommentId(commentId);
        if(!Objects.equals(commenterUserId, userId)) {
            throw new IllegalArgumentException(ERROR_NO_PERMISSION);
        }
    }

    private String findCommenterIdByCommentId(Long commentId) {
        return getComment(commentId).getCommenter().getUserId();
    }

    private User getUserByUserId(String id) {
        return userRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_USER_NOT_FOUND));
    }

    private Board getBoardByBoardId(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_BOARD_NOT_FOUND));
    }
}
