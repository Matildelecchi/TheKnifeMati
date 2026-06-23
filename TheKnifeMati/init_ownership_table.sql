-- Create proprietari_ristoranti table to store ownership relationships
-- Run this script in PostgreSQL to initialize the table

CREATE TABLE IF NOT EXISTS proprietari_ristoranti (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL REFERENCES utenti(username) ON DELETE CASCADE,
    ristorante VARCHAR(255) NOT NULL REFERENCES ristoranti(nome) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(username, ristorante)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_proprietari_username ON proprietari_ristoranti(username);
CREATE INDEX IF NOT EXISTS idx_proprietari_ristorante ON proprietari_ristoranti(ristorante);
