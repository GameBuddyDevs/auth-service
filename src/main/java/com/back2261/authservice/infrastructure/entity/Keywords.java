package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.Set;
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
public class Keywords {
    @Id
    private UUID id;

    private String keywordName;

    @CreationTimestamp
    private Date createdDate;

    @ManyToMany(mappedBy = "keywords")
    private Set<Gamer> gamers;
}
