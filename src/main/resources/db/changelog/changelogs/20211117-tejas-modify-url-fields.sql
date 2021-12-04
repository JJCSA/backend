--liquibase formatted sql

--changeset tejas:modify-url-fields

alter table user_account alter column linkedin_url type varchar(255);
alter table user_account alter column community_document_url type varchar(255);
alter table user_account alter column profile_picture_url type varchar(255);
