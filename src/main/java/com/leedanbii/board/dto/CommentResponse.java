package com.leedanbii.board.dto;

import com.leedanbii.board.domain.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private String commenterUserId;
    private String commenterName;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCommenter().getUserId(),
                comment.getCommenter().getName(),
                comment.getCreatedAt()
        );
    }
}
