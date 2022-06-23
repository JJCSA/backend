DROP VIEW IF EXISTS V_JJC_SEARCH;

CREATE VIEW V_JJC_SEARCH AS
   SELECT
       u.ID AS USER_ID,
       CONCAT(u.FIRST_NAME ,' ', u.MIDDLE_NAME,' ', u.LAST_NAME) as NAME,
       u.STATE,
       u.CITY,
       e.SPECIALIZATION,
       e.UNIVERSITY_NAME,
       w.ROLE AS WORK_ROLE,
       u.LINKEDIN_URL
           FROM USER_ACCOUNT u
       LEFT OUTER JOIN EDUCATION e
            ON e.USER_ID = u.ID
       LEFT OUTER JOIN WORK_EX w
            ON w.USER_ID = u.ID
       WHERE u.user_status = 'Active';