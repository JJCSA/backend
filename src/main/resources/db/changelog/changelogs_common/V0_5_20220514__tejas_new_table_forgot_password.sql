--liquibase formatted sql

--changeset tejas:new-table-forgot-password

CREATE TABLE IF NOT EXISTS user_temp_password(
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    temp_password VARCHAR(60),
    created_time TIMESTAMP,
    expiration_time TIMESTAMP
);
