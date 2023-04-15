package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "avatars", schema = "schappl")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Avatars {
    @Id
    private UUID id;

    private String image;
    private Boolean isSpecial;
}
