package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.Gamer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamerRepository extends JpaRepository<Gamer, String> {
    Optional<Gamer> findByGamerUsername(String username);

    Optional<Gamer> findByEmail(String email);

    List<Gamer> findAllByIsBlockedTrue();
}
