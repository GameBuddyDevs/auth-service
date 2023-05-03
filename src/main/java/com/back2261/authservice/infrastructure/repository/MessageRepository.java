package com.back2261.authservice.infrastructure.repository;

import com.back2261.authservice.infrastructure.entity.Message;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findAllByIsReportedTrue();

    Optional<Message> findByIdAndIsReportedTrue(String id);
}
