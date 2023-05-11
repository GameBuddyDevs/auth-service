package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<Games, String> {}
