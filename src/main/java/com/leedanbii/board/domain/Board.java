package com.leedanbii.board.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private static final String ERROR_TITLE_LENGTH = "제목은 1~%d자여야 합니다.";
    private static final String ERROR_CONTENTS_LENGTH = "내용은 1~%d자여야 합니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private Board(String title, String content, User writer) {
        title = title.trim();
        content = content.trim();
        validateInput(title, content);
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Board of(String title, String content, User writer) {
        return new Board(title, content, writer);
    }

    public void update(String newTitle, String newContent) {
        newTitle = newTitle.trim();
        newContent = newContent.trim();
        validateInput(newTitle, newContent);
        this.title = newTitle;
        this.content = newContent;
    }

    public void validateInput(String title, String content) {
        validateTitle(title);
        validateContent(content);
    }

    private void validateTitle(String title) {
        if (title.isBlank() || title.length() > TITLE_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_TITLE_LENGTH, TITLE_LENGTH_MAX));
        }
    }

    private void validateContent(String content) {
        if (content.isBlank() || content.length() > CONTENTS_LENGTH_MAX) {
            throw new IllegalArgumentException(String.format(ERROR_CONTENTS_LENGTH, CONTENTS_LENGTH_MAX));
        }
    }
}
