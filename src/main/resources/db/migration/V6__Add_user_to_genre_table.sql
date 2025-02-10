CREATE TABLE IF NOT EXISTS user_to_genre(
    id SERIAL PRIMARY KEY NOT NULL,
    genre_name varchar(255) NOT NULL REFERENCES genres(name),
    user_id BIGINT NOT NULL REFERENCES users(id)
);