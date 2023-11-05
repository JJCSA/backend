--liquibase formatted sql

--changeset tejas:user-account-new-fields

ALTER TABLE user_account
    ADD COLUMN IF NOT EXISTS gender VARCHAR(20),
    ADD COLUMN IF NOT EXISTS about_me VARCHAR(500),
    ADD COLUMN IF NOT EXISTS is_regional_contact BOOLEAN;
