CREATE DATABASE IF NOT EXISTS cloud_watch_party;
USE cloud_watch_party;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(254) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    display_name VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(6) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    host_id BIGINT NOT NULL,
    video_url VARCHAR(1000),
    playing BOOLEAN NOT NULL DEFAULT FALSE,
    position_seconds DOUBLE NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_room_host FOREIGN KEY (host_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS room_members (
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (room_id, user_id),
    CONSTRAINT fk_member_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_member_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    text VARCHAR(500) NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_message_room FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_message_author FOREIGN KEY (author_id) REFERENCES users(id)
);
