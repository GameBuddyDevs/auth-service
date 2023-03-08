package com.back2261.authservice.infrastructure.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "gamer", schema = "schauth")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String userId;

    @Column(unique = true)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private Integer age;
    private String country;
    private byte[] avatar;

    @CreationTimestamp
    private Date createdDate;

    @UpdateTimestamp
    private Date lastModifiedDate;

    private String pwd;
    private String gender;
    private Boolean isBlocked = false;
    private Boolean isRegistered = false;

    @ManyToMany
    @JoinTable(
            name = "gamer_keywords_join",
            schema = "schauth",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id"))
    private Set<Keywords> keywords;

    private Boolean isVerified = false;

    @ManyToMany
    @JoinTable(
            name = "gamer_games_join",
            schema = "schauth",
            joinColumns = @JoinColumn(name = "gamer_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"))
    private Set<Games> likedgames;
}
