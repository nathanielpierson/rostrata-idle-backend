-- Ensure image_url can store long image URLs.
ALTER TABLE IF EXISTS trees
    ALTER COLUMN image_url TYPE TEXT
    USING image_url::TEXT;
