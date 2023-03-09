package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
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
public class Games {
    @Id
    private String gameId;

    private String gameName;
    private byte[] gameIcon;
    private String category;
    private Float avgVote;
    private String description;

    @ManyToMany(mappedBy = "likedgames")
    private Set<Gamer> gamers;
}
