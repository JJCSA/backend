DO
$do$
BEGIN
    IF NOT EXISTS(
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_NAME = 'jjc_search_view'
    ) THEN

    CREATE OR REPLACE VIEW JJC_SEARCH_VIEW AS
        SELECT
            u.ID AS USER_ID,
            CONCAT(u.FIRST_NAME ,' ', u.MIDDLE_NAME,' ', u.LAST_NAME) as FULL_NAME,
            u.STATE AS LIVING_STATE,
            u.CITY AS LIVING_CITY,
            e.SPECIALIZATION AS WORK_SPECIALIZATION,
            e.UNIVERSITY_NAME AS UNIVERSITY,
            w.ROLE AS WORK_ROLE
        FROM USER_ACCOUNT u
            LEFT OUTER JOIN EDUCATION e
                on e.USER_ID = u.ID
            LEFT OUTER JOIN WORK_EX w
                on w.USER_ID = u.ID;
    END IF;

END
$do$