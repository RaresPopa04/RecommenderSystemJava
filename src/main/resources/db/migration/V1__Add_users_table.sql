CREATE TABLE IF NOT EXISTS users(
    id SERIAL PRIMARY KEY,
    file_id BIGINT UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);