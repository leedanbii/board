package com.leedanbii.board.dto;

import com.leedanbii.board.domain.Board;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardResponse {
    private Long id;
    private String title;
    private String writerName;
    private OffsetDateTime createdAt;

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getWriter().getName(),
                board.getCreatedAt()
        );
    }
}
