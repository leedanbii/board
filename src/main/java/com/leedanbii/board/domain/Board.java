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
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "boards")
public class Board {

    private static final long TITLE_LENGTH_MAX = 30;
    private static final long CONTENTS_LENGTH_MAX = 1000;
    private static final String ERROR_TITLE_TOO_LONG = "제목은 %d자를 초과할 수 없습니다.";
    private static final String ERROR_CONTENTS_TOO_LONG = "내용은 %d자를 초과할 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    private Board(String title, String content, User writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Board of(String title, String content, User writer) {
        validateTitle(title);
        validateContent(content);
        return new Board(title, content, writer);
    }

    public void update(String newTitle, String newContent) {
        validateTitle(newTitle);
        validateContent(newContent);
        this.title = newTitle;
        this.content = newContent;
    }

    public static void validateTitle(String title) {
        if (title.length() > TITLE_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_TITLE_TOO_LONG, TITLE_LENGTH_MAX));
        }
    }

    public static void validateContent(String content) {
        if (content.length() > CONTENTS_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_CONTENTS_TOO_LONG, CONTENTS_LENGTH_MAX));
        }
    }
}
