DROP TABLE IF EXISTS vote;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS restaurant;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id       INT                   DEFAULT nextval('global_seq'),
    login    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT users_pk
        PRIMARY KEY (id)
);

CREATE UNIQUE INDEX users_email_uindex
    ON users (email);

CREATE TABLE user_role
(
    user_id INTEGER NOT NULL,
    role    VARCHAR(255),
    CONSTRAINT user_role_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurant
(
    id   INT DEFAULT nextval('global_seq'),
    name VARCHAR(255) NOT NULL,
    CONSTRAINT restaurant_pk
        PRIMARY KEY (id),
    CONSTRAINT restaurant_uq_name UNIQUE (name)
);

CREATE TABLE dish
(
    id            INT DEFAULT nextval('global_seq'),
    description   TEXT(255) NOT NULL,
    price         BIGINT    NOT NULL,
    restaurant_id INT       NOT NULL,
    CONSTRAINT dish_pk
        PRIMARY KEY (id),
    CONSTRAINT dish_restaurant_id_fk
        FOREIGN KEY (restaurant_id) REFERENCES restaurant
            ON DELETE CASCADE
);

CREATE TABLE vote
(
    id            INT  DEFAULT nextval('global_seq'),
    vote_date     DATE DEFAULT today        NOT NULL,
    vote_time     TIME DEFAULT current_time NOT NULL,
    user_id       INT                       NOT NULL,
    restaurant_id INT                       NOT NULL,

    CONSTRAINT vote_restaurant_id_fk
        FOREIGN KEY (restaurant_id) REFERENCES restaurant (id)
            ON DELETE CASCADE,
    CONSTRAINT vote_users_id_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE,
    CONSTRAINT vote_uq_user_id_date UNIQUE (vote_date, user_id)
);








