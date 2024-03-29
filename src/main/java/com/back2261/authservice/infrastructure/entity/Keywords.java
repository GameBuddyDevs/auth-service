package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "keywords")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Keywords implements Serializable {
    @Id
    private UUID id;

    private String keywordName;

    @CreationTimestamp
    private Date createdDate;

    private String description;
}
