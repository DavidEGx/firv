-- Creo las tablas
CREATE TABLE IF NOT EXISTS configuration
(
    property integer,
    value varchar(10000)
);

CREATE TABLE IF NOT EXISTS videos
(
    video_hash varchar(1000) PRIMARY KEY,
    video_name varchar(1000),
    video_path varchar(1000)
);

CREATE TABLE IF NOT EXISTS images
(
    image_haar varchar(1000),
    image_number integer,
    video_hash varchar(1000) REFERENCES videos(Hash),
    image_path varchar(1000)
);

-- Creo los indices
CREATE INDEX IF NOT EXISTS  IX_Images_Haar on images (image_haar);