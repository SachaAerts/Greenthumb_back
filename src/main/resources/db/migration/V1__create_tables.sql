CREATE TABLE roles
(
    id_role SERIAL PRIMARY KEY,
    label   VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE users
(
    id_user      SERIAL PRIMARY KEY,
    firstname    varchar(100)        NOT NULL,
    lastname     varchar(100)        NOT NULL,
    username     varchar(100) UNIQUE NOT NULL,
    mail         varchar(250) UNIQUE NOT NULL ,
    password     varchar(250)        NOT NULL,
    phone_number varchar(20),
    biography    TEXT,
    is_private   BOOLEAN DEFAULT FALSE,
    id_role      INT NOT NULL REFERENCES roles (id_role)
);

CREATE TABLE plants
(
    id_plant        SERIAL PRIMARY KEY,
    slug            VARCHAR(100) UNIQUE NOT NULL,
    scientific_name VARCHAR(255)        NOT NULL,
    common_name     VARCHAR(255),
    life_cycle      VARCHAR(100),
    water_need      VARCHAR(100),
    light           VARCHAR(100),
    soil_type       VARCHAR(100),
    soil_ph_min     NUMERIC(3, 1),
    soil_ph_max     NUMERIC(3, 1),
    temperature_min INTEGER,
    temperature_max INTEGER,
    humidity_need   VARCHAR(100),
    bloom_months    VARCHAR(100),
    pet_toxic       BOOLEAN DEFAULT FALSE,
    human_toxic     BOOLEAN DEFAULT FALSE,
    indoor_friendly BOOLEAN DEFAULT TRUE,
    image_url       TEXT,
    description     TEXT,
    id_user         INT NOT NULL REFERENCES users (id_user)
);

CREATE TABLE ressources
(
    id_resource   SERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    light         VARCHAR(100),
    picture       TEXT,
    text          TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_user       INT NOT NULL REFERENCES users (id_user)
);

CREATE TABLE messages
(
    id_message  SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    text        TEXT,
    number_like INT DEFAULT 0
);

CREATE TABLE commentaries
(
    id_commentary SERIAL PRIMARY KEY,
    text          TEXT NOT NULL,
    id_message    INT NOT NULL REFERENCES messages (id_message)
);

CREATE TABLE medias
(
    id_picture SERIAL PRIMARY KEY,
    url        TEXT NOT NULL,
    id_commentary INT NOT NULL REFERENCES commentaries (id_commentary),
    id_message INT NOT NULL REFERENCES messages (id_message)
);

CREATE TABLE categories
(
    id_tag SERIAL PRIMARY KEY,
    label  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE plants_api (
                           id_plant SERIAL PRIMARY KEY,
                           slug VARCHAR(100) UNIQUE NOT NULL,
                           scientific_name VARCHAR(255),
                           common_name VARCHAR(255),
                           life_cycle VARCHAR(100),
                           water_need VARCHAR(100),
                           soil_type VARCHAR(100),
                           soil_ph_min NUMERIC(3,1),
                           soil_ph_max NUMERIC(3,1),
                           temperature_min INTEGER,
                           temperature_max INTEGER,
                           bloom_months VARCHAR(100),
                           pet_toxic BOOLEAN DEFAULT FALSE,
                           human_toxic BOOLEAN DEFAULT FALSE,
                           indoor_friendly BOOLEAN DEFAULT TRUE,
                           image_url TEXT,
                           description TEXT
);

CREATE TABLE post
(
    id_user    INT NOT NULL REFERENCES users (id_user),
    id_message INT NOT NULL REFERENCES messages (id_message),
    is_like    BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id_user, id_message)
);

CREATE TABLE reply
(
    id_user       INT NOT NULL REFERENCES users (id_user),
    id_commentary INT NOT NULL REFERENCES commentaries (id_commentary),
    is_like       BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id_user, id_commentary)
);

CREATE TABLE tag
(
    id_message       INT NOT NULL REFERENCES messages (id_message),
    id_tag           INT NOT NULL REFERENCES categories (id_tag),
    PRIMARY KEY (id_message, id_tag)
);

