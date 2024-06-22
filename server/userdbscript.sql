-- Database: chatapp

-- DROP DATABASE IF EXISTS chatapp;

CREATE DATABASE chatapp
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;


CREATE TABLE users (
    uid serial PRIMARY KEY,
    username varchar(50) NOT NULL,
    usermail varchar(50) NOT NULL,
    userpass varchar(50) NOT NULL
);

INSERT INTO users (username, usermail, userpass)
VALUES
    ('Nixon', 'nixondebantu@gmail.com', '123456'),
    ('Antu', 'nixon2002@gmail.com', '123456'),
	('CharUser', 'aaa', '123'),
	('INT User', '123', '123');

SELECT * FROM users;

CREATE TABLE chats (
    chat_id SERIAL PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

