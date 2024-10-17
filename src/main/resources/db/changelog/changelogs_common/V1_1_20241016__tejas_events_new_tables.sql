--liquibase formatted sql

--changeset tejas:events-new-tables

CREATE TYPE event_status AS ENUM('upcoming', 'ongoing', 'past');

CREATE TABLE IF NOT EXISTS events
(
    id                    VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid(),
    title                 VARCHAR(255) NOT NULL,
    short_description     TEXT,
    long_description      TEXT,
    location              VARCHAR(255),
    start_time            TIMESTAMP    NOT NULL,
    end_time              TIMESTAMP    NOT NULL,
    is_published          BOOLEAN      NOT NULL    DEFAULT FALSE,
    registration_deadline TIMESTAMP,
    meeting_link          TEXT,
    status                event_status             DEFAULT 'upcoming',
    created_by_user       VARCHAR(255) NOT NULL,
    created_at            TIMESTAMP    NOT NULL    DEFAULT NOW(),
    updated_at            TIMESTAMP    NOT NULL    DEFAULT NOW()
);

CREATE TYPE event_response AS ENUM('yes', 'no', 'maybe');

CREATE TABLE IF NOT EXISTS user_event_responses
(
    id              VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id        VARCHAR(255)   NOT NULL
        CONSTRAINT fk_events__user_event_responses
            REFERENCES events,
    user_id         VARCHAR(255)   NOT NULL
        CONSTRAINT fk_user_account__user_event_responses
            REFERENCES user_account,
    response        event_response NOT NULL,
    created_by_user VARCHAR(255)   NOT NULL,
    created_at      TIMESTAMP      NOT NULL  DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL  DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS event_speaker
(
    id                  VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id            VARCHAR(255) NOT NULL
        CONSTRAINT fk_events__user_event_responses
            REFERENCES events,
    speaker_name        VARCHAR(50)  NOT NULL,
    speaker_description TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS event_resources
(
    id               VARCHAR(255) PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id         VARCHAR(255) NOT NULL
        CONSTRAINT fk_events__user_event_responses
            REFERENCES events,
    resource_type    VARCHAR(50),
    resource_s3_name TEXT         NOT NULL -- format "<UUID>.<file_ext>"
);
