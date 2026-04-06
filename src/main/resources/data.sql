MERGE INTO status (status_name) KEY(status_name)
VALUES ('ACCEPTED'), ('PENDING'), ('REJECTED'), ('DELETED');

MERGE INTO rating (rating_name) KEY(rating_name)
VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

MERGE INTO genre (genre_name) KEY(genre_name)
VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');