package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.Keywords;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordsRepository extends JpaRepository<Keywords, String> {
    Optional<Keywords> findByKeywordName(String keywordName);
}
