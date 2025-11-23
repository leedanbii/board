package com.leedanbii.board.repository;

import com.leedanbii.board.domain.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("SELECT b FROM Board b JOIN FETCH b.writer ORDER BY b.createdAt DESC")
    List<Board> findAllWithWriter();

    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.comments c LEFT JOIN FETCH b.writer WHERE b.id = :id")
    Optional<Board> findByIdWithComments(@Param("id") Long id);
}
