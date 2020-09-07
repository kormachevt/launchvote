DELETE
FROM votes;
DELETE
FROM user_roles;
DELETE
FROM users;
DELETE
FROM dishes;
DELETE
FROM restaurants;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (login, email, password)
VALUES ('User', 'user@gmail.com', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001),
       ('USER', 100001);

INSERT INTO RESTAURANTS (name)
VALUES ('Alfa'),
       ('Omega');

INSERT INTO DISHES (description, price, restaurant_id)
VALUES ('Alfa_1', 201, 100002),
       ('Alfa_2', 202, 100002),
       ('Omega_1', 301, 100003),
       ('Omega_2', 302, 100003);

INSERT INTO VOTES (DATE, TIME, USER_ID, RESTAURANT_ID)
VALUES ('2020-09-02', '00:00:00', 100000, 100002),
       ('2020-09-03', '00:00:00', 100000, 100002),
       ('2020-09-03', '23:59:59', 100001, 100002),
       ('2020-09-04', '23:59:59', 100001, 100003);