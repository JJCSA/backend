--liquibase formatted sql

--changeset tejas:db-id-default-uuid

ALTER TABLE user_account
    ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE admin_action
    ALTER COLUMN action_id SET DEFAULT gen_random_uuid();
ALTER TABLE email_templates
    ALTER COLUMN template_id SET DEFAULT gen_random_uuid();
ALTER TABLE education
    ALTER COLUMN educ_id SET DEFAULT gen_random_uuid();
ALTER TABLE work_ex
    ALTER COLUMN exp_id SET DEFAULT gen_random_uuid();

