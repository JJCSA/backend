--liquibase formatted sql

--changeset tejas:user-account-new-fields

ALTER TABLE user_account
    ADD COLUMN IF NOT EXISTS country varchar(45),
    ADD COLUMN IF NOT EXISTS is_user_student BOOLEAN default true;
