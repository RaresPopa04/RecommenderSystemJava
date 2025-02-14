package com.recommender.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.recommender.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", unique = true)
    private Long fileId;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @ManyToMany
            @JoinTable(name = "user_to_genre",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_name"))
            @JsonManagedReference
    List<GenreEntity> preferredGenres;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<RatingEntity> ratings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public User fromEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setFileId(this.fileId);
        user.setGenres(preferredGenres.stream().map(GenreEntity::getName).toList());
        return user;
    }
}
