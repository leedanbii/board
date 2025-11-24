package com.leedanbii.board.dto;

import com.leedanbii.board.domain.Board;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private String writerUserId;
    private OffsetDateTime createdAt;
    private List<CommentResponse> comments;

    public static BoardDetailResponse from(Board board) {
        List<CommentResponse> commentResponses = board.getComments().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toList());

        return new BoardDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getWriter().getName(),
                board.getWriter().getUserId(),
                board.getCreatedAt(),
                commentResponses
        );
    }
}
