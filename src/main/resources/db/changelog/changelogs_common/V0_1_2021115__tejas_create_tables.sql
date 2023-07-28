--liquibase formatted sql

--changeset tejas:create-tables

create table if not exists user_account
(
    id varchar(255) not null
        constraint user_account_pkey
            primary key,
    date_approved varchar(45) default '01/01/1900',
    city varchar(45),
    community_document_url varchar(100),
    community_name varchar(100),
    contact_method varchar(45) default 'Whatsapp',
    contact_share boolean default false,
    dob varchar(45) default '01/01/1900',
    description varchar(256),
    email varchar(255),
    first_name varchar(45),
    last_name varchar(45),
    last_updated_date varchar(45) default '01/01/1900',
    linkedin_url varchar(100),
    loan_organization varchar(45),
    loan_taken boolean default false,
    middle_name varchar(45),
    mobile varchar(20),
    profile_picture_url varchar(100),
    socialmedia_platform varchar(45),
    state varchar(45),
    street varchar(45),
    user_status varchar(45),
    volunteering_interest varchar(45),
    zip varchar(10)
);

create table if not exists admin_action
(
    action_id uuid not null
        constraint admin_action_pkey
            primary key,
    action varchar(45) default '',
    date_of_action timestamp with time zone,
    descrip varchar(512) default '',
    from_user_id varchar(255),
    to_user_id varchar(255)
);

create table if not exists email_templates
(
    template_id uuid not null default uuid_generate_v1()
        constraint email_templates_pkey
            primary key,
    email_body text default '',
    email_subject varchar(255) default '',
    template_name varchar(255) default ''
        constraint uk_asgn7km5k3b63c4tqunx8jd8s
            unique
);

create table if not exists education
(
    educ_id uuid not null
        constraint education_pkey
            primary key,
    degree varchar(100),
    grad_month integer default 11,
    grad_year integer default 1111,
    specialization varchar(45),
    university_name varchar(45),
    user_id varchar(255)
        constraint fkhj3n1q7kmb844hp3vu0javtkg
            references user_account
);

create table if not exists work_ex
(
    exp_id uuid not null
        constraint work_ex_pkey
            primary key,
    company_name varchar(45),
    location varchar(45),
    role varchar(45) ,
    total_exp varchar(5),
    user_id varchar(255)
        constraint fk2dgquftoc90766c9bp1f20lvf
            references user_account
);

