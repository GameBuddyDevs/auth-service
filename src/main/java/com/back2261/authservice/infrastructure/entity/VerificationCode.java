package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "verification_code")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationCode {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", insertable = false, updatable = false, nullable = false, unique = true)
    private UUID id;

    private Integer code;
    private String email;
    private Boolean isValid;
}
