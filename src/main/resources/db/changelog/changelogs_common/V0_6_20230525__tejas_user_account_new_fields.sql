--liquibase formatted sql

--changeset tejas:user-account-new-fields

ALTER TABLE user_account
    ADD COLUMN IF NOT EXISTS gender varchar(20),
    ADD COLUMN IF NOT EXISTS about_me varchar(500);
