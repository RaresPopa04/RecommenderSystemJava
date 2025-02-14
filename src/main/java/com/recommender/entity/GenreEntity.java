package com.recommender.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres")
@ToString
@EqualsAndHashCode
@Data
public class GenreEntity {

    @Id
    @Column(name = "name", nullable = false, length = 255)
    private String name;

}
