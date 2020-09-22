DELETE
FROM vote;
DELETE
FROM user_role;
DELETE
FROM users;
DELETE
FROM dish;
DELETE
FROM restaurant;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (login, email, password)
VALUES ('User', 'user@gmail.com', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001),
       ('USER', 100001);

INSERT INTO restaurant (name)
VALUES ('Alfa'),
       ('Omega'),
       ('Beta');

INSERT INTO dish (description, price, restaurant_id)
VALUES ('Alfa_1', 201, 100002),
       ('Alfa_2', 202, 100002),
       ('Omega_1', 301, 100003),
       ('Omega_2', 302, 100003);

INSERT INTO vote (vote_date, vote_time, user_id, restaurant_id)
VALUES (today - 1, '00:00:00', 100000, 100002),
       (today, '00:00:00', 100000, 100002),
       (today, '23:59:59', 100001, 100003),
       (today + 1, '23:59:59', 100001, 100003);