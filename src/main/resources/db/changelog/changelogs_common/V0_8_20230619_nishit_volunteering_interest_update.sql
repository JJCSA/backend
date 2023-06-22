--liquibase formatted sql

--changeset nishit:update-volunteering-interest

ALTER TABLE user_account
   ALTER COLUMN volunteering_interest TYPE varchar(200);