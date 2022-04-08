--liquibase formatted sql

--changeset tejas:user-account-new-fields

ALTER TABLE user_account
    ADD COLUMN country varchar(45),
    ADD COLUMN is_user_student BOOLEAN default true;
