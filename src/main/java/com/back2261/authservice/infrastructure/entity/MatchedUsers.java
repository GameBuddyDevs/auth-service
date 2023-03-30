package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "approved_matches", schema = "schmatch")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MatchedUsers {

    @Id
    @Column(name = "matched_id")
    private String matchedId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Gamer gamer;
}
