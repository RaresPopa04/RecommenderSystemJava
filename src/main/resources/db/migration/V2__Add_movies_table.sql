CREATE TABLE IF NOT EXISTS movies(
    ID SERIAL PRIMARY KEY NOT NULL,
    file_id BIGINT UNIQUE,
    title VARCHAR(300) NOT NULL,
    imdb_id VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);