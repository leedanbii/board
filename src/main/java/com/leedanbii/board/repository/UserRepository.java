package com.leedanbii.board.repository;

import com.leedanbii.board.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
}
