package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "games")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Games implements Serializable {
    @Id
    private String gameId;

    private String gameName;
    private String gameIcon;
    private String category;
    private Float avgVote;
    private String description;
}
