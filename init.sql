-- Tabelle für User
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabelle für MediaEntry
CREATE TABLE media_entries (
                               id UUID PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               description TEXT,
                               media_type VARCHAR(50) NOT NULL,
                               release_year INTEGER NOT NULL,
                               age_restriction INTEGER NOT NULL,
                               average_score DOUBLE PRECISION DEFAULT 0.0,
                               creator_id UUID NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabelle für Genres (Many-to-Many Beziehung)
CREATE TABLE media_genres (
                              media_id UUID NOT NULL,
                              genre VARCHAR(100) NOT NULL,
                              PRIMARY KEY (media_id, genre),
                              FOREIGN KEY (media_id) REFERENCES media_entries(id) ON DELETE CASCADE
);

-- Tabelle für Tokens
CREATE TABLE tokens (
                        token VARCHAR(255) PRIMARY KEY,
                        user_id UUID NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indices für bessere Performance
CREATE INDEX idx_media_title ON media_entries(title);
CREATE INDEX idx_media_type ON media_entries(media_type);
CREATE INDEX idx_media_creator ON media_entries(creator_id);
CREATE INDEX idx_tokens_user ON tokens(user_id);