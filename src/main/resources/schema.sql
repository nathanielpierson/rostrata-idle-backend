-- Ensure image_url can store long image URLs.
ALTER TABLE IF EXISTS trees
    ALTER COLUMN image_url TYPE TEXT
    USING image_url::TEXT;

ALTER TABLE IF EXISTS trees
    ADD COLUMN IF NOT EXISTS xp_given INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS woodcutting_xp BIGINT NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS fishing_xp BIGINT NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS mining_xp BIGINT NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS hunting_xp BIGINT NOT NULL DEFAULT 0;

-- fish.image_url: long URLs (after Hibernate creates the table on first run).
ALTER TABLE IF EXISTS fish
    ALTER COLUMN image_url TYPE TEXT
    USING image_url::TEXT;
