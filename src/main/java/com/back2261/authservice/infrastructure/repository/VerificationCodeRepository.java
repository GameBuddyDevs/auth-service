package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.VerificationCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {
    List<VerificationCode> findAllByEmail(String email);

    Optional<VerificationCode> findByEmailAndCodeAndIsValidTrue(String email, Integer code);
}
