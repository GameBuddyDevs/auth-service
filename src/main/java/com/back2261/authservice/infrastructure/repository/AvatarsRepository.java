package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.Avatars;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarsRepository extends JpaRepository<Avatars, UUID> {}
