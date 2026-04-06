INSERT INTO status (status_name)
SELECT 'ACCEPTED'
FROM DUAL
WHERE (SELECT COUNT(*) FROM status) = 1;

INSERT INTO status (status_name)
SELECT 'PENDING'
FROM DUAL
WHERE (SELECT COUNT(*) FROM status) = 2;

INSERT INTO status (status_name)
SELECT 'REJECTED'
FROM DUAL
WHERE (SELECT COUNT(*) FROM status) = 3;

INSERT INTO status (status_name)
SELECT 'DELETED'
FROM DUAL
WHERE (SELECT COUNT(*) FROM status) = 4;


INSERT INTO rating (rating_name)
SELECT 'G'
FROM DUAL
WHERE (SELECT COUNT(*) FROM rating) = 1;

INSERT INTO rating (rating_name)
SELECT 'PG'
FROM DUAL
WHERE (SELECT COUNT(*) FROM rating) = 2;

INSERT INTO rating (rating_name)
SELECT 'PG-13'
FROM DUAL
WHERE (SELECT COUNT(*) FROM rating) = 3;

INSERT INTO rating (rating_name)
SELECT 'R'
FROM DUAL
WHERE (SELECT COUNT(*) FROM rating) = 4;

INSERT INTO rating (rating_name)
SELECT 'NC-17'
FROM DUAL
WHERE (SELECT COUNT(*) FROM rating) = 5;


INSERT INTO genre (genre_name)
SELECT 'Комедия'
FROM DUAL
WHERE (SELECT COUNT(*) FROM genre) = 1;

INSERT INTO genre (genre_name)
SELECT 'Драма'
FROM DUAL
WHERE (SELECT COUNT(*) FROM genre) = 2;

INSERT INTO genre (genre_name)
SELECT 'Мультфильм'
FROM DUAL
WHERE (SELECT COUNT(*) FROM genre) = 3;

INSERT INTO genre (genre_name)
SELECT 'Триллер'
FROM DUAL
WHERE (SELECT COUNT(*) FROM genre) = 4;

INSERT INTO genre (genre_name)
SELECT 'Документальный'
FROM DUAL
WHERE (SELECT COUNT(*) FROM genre) = 5;

INSERT INTO genre (genre_name)
SELECT 'Боевик'
FROM DUAL
WHERE (SELECT COUNT(*) FROM genre) = 6;