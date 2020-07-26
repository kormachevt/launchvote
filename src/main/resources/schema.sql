DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS restaurants;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100000;

CREATE TABLE users
(
    id       INT DEFAULT nextval('global_seq'),
    login    VARCHAR NOT NULL,
    password VARCHAR NOT NULL,
    email    VARCHAR NOT NULL,
    CONSTRAINT USERS_PK
        PRIMARY KEY (id)
);

CREATE UNIQUE INDEX USERS_EMAIL_UINDEX
    ON users (email);

CREATE TABLE user_roles
(
    user_id INTEGER NOT NULL,
    role    VARCHAR,
    CONSTRAINT user_roles_idx UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE restaurants
(
    id   INT DEFAULT nextval('global_seq'),
    name TEXT NOT NULL,
    CONSTRAINT RESTAURANTS_PK
        PRIMARY KEY (id)
);

CREATE TABLE dishes
(
    id          INT DEFAULT nextval('global_seq'),
    description TEXT           NOT NULL,
    price       DECIMAL(20, 2) NOT NULL,
    restaurant  INT            NOT NULL,
    CONSTRAINT DISHES_PK
        PRIMARY KEY (id),
    CONSTRAINT DISHES_RESTAURANTS_ID_FK
        FOREIGN KEY (restaurant) REFERENCES RESTAURANTS
            ON DELETE CASCADE
);

CREATE TABLE votes
(
    id         INT       DEFAULT nextval('global_seq'),
    date_time  TIMESTAMP DEFAULT now() NOT NULL,
    user       INT                     NOT NULL,
    restaurant INT                     NOT NULL,
    CONSTRAINT VOTES_RESTAURANTS_ID_FK
        FOREIGN KEY (restaurant) REFERENCES RESTAURANTS
            ON DELETE CASCADE,
    CONSTRAINT VOTES_USERS_ID_FK
        FOREIGN KEY (user) REFERENCES USERS
            ON DELETE CASCADE
);









