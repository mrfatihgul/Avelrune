CREATE TABLE inquiries (
    id             BIGSERIAL PRIMARY KEY,
    full_name      VARCHAR(120) NOT NULL,
    phone          VARCHAR(40)  NOT NULL,
    email          VARCHAR(180) NOT NULL,
    message        VARCHAR(3000) NOT NULL,
    property_slug  VARCHAR(180),
    property_id    BIGINT,
    owner_user_id  BIGINT,
    sender_user_id BIGINT,
    read_flag      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
