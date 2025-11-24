package com.leedanbii.board.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    private static final long CONTENTS_LENGTH_MAX = 300;
    private static final String ERROR_CONTENTS_LENGTH = "내용은 1~%d자여야 합니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User commenter;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    private Comment(String content, Board board, User commenter) {
        content = content.trim();
        validateContent(content);
        this.content = content;
        this.board = board;
        this.commenter = commenter;
    }

    public static Comment of(String content, Board board, User commenter) {
        return new Comment(content, board, commenter);
    }

    private void validateContent(String content) {
        if (content.isBlank() || content.length() > CONTENTS_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_CONTENTS_LENGTH, CONTENTS_LENGTH_MAX));
        }
    }
}
