CREATE TABLE admin_users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(180) NOT NULL UNIQUE,
    full_name       VARCHAR(120) NOT NULL,
    password_hash   VARCHAR(120) NOT NULL,
    role            VARCHAR(40)  NOT NULL,
    created_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL
);
