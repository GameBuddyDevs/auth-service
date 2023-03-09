package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    List<Session> findByEmailAndIsActiveTrue(String username);

    Optional<Session> findByAccessTokenAndIsActiveTrue(String accessToken);
}
