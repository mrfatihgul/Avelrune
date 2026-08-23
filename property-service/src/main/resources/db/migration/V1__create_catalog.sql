CREATE TABLE agents (
    id          BIGSERIAL PRIMARY KEY,
    full_name   VARCHAR(120) NOT NULL,
    title       VARCHAR(160) NOT NULL,
    bio         VARCHAR(800) NOT NULL,
    phone       VARCHAR(40)  NOT NULL,
    email       VARCHAR(180) NOT NULL,
    photo_url   VARCHAR(260) NOT NULL,
    expertise   VARCHAR(160) NOT NULL,
    sort_order  INTEGER      NOT NULL
);

CREATE TABLE properties (
    id                BIGSERIAL PRIMARY KEY,
    title             VARCHAR(180) NOT NULL,
    slug              VARCHAR(200) NOT NULL,
    description       VARCHAR(6000) NOT NULL,
    price             NUMERIC(14, 0) NOT NULL,
    listing_type      VARCHAR(20) NOT NULL,
    property_type     VARCHAR(30) NOT NULL,
    city              VARCHAR(80) NOT NULL,
    district          VARCHAR(80) NOT NULL,
    neighborhood      VARCHAR(80) NOT NULL,
    gross_area        INTEGER NOT NULL,
    net_area          INTEGER NOT NULL,
    room_count        VARCHAR(20) NOT NULL,
    building_age      INTEGER NOT NULL,
    floor             INTEGER NOT NULL,
    total_floors      INTEGER NOT NULL,
    heating_type      VARCHAR(30) NOT NULL,
    furnished         BOOLEAN NOT NULL,
    latitude          DOUBLE PRECISION NOT NULL,
    longitude         DOUBLE PRECISION NOT NULL,
    featured          BOOLEAN NOT NULL,
    active            BOOLEAN NOT NULL,
    features          VARCHAR(800),
    agent_id          BIGINT REFERENCES agents (id),
    assigned_user_id  BIGINT,
    created_at        TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX idx_property_slug ON properties (slug);
CREATE INDEX idx_property_city ON properties (city);
CREATE INDEX idx_property_listing ON properties (listing_type);

CREATE TABLE property_images (
    id           BIGSERIAL PRIMARY KEY,
    property_id  BIGINT NOT NULL REFERENCES properties (id) ON DELETE CASCADE,
    url          VARCHAR(260) NOT NULL,
    alt_text     VARCHAR(180) NOT NULL,
    sort_order   INTEGER NOT NULL
);
