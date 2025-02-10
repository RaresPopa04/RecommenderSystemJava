CREATE TABLE IF NOT EXISTS movie_to_genre(
    id SERIAL PRIMARY KEY NOT NULL,
    genre_name varchar(255) NOT NULL REFERENCES genres(name),
    movie_id BIGINT NOT NULL REFERENCES movies(id)
);