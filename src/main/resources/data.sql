delete
from votes;
delete
from user_roles;
delete
from users;
delete
from dishes;
delete
from restaurants;
alter sequence global_seq RESTART with 100000;

insert into users (login, email, password)
values ('User', 'user@gmail.com', '{noop}password'),
       ('Admin', 'admin@gmail.com', '{noop}admin');

insert into user_roles (role, user_id)
values ('USER', 100000),
       ('ADMIN', 100001),
       ('USER', 100001);

insert into RESTAURANTS (name)
values ('Alfa'),
       ('Omega'),
       ('Beta');

insert into DISHES (description, price, restaurant_id)
values ('Alfa_1', 201, 100002),
       ('Alfa_2', 202, 100002),
       ('Omega_1', 301, 100003),
       ('Omega_2', 302, 100003);

insert into VOTES (DATE, TIME, USER_ID, RESTAURANT_ID)
values (today - 1, '00:00:00', 100000, 100002),
       (today, '00:00:00', 100000, 100002),
       (today, '23:59:59', 100001, 100003),
       (today + 1, '23:59:59', 100001, 100003);