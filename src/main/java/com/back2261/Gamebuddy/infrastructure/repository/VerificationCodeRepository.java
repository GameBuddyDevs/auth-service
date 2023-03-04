package com.back2261.Gamebuddy.infrastructure.repository;

import com.back2261.Gamebuddy.infrastructure.entity.VerificationCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {
    List<VerificationCode> findAllByEmail(String email);
}
