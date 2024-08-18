DELETE FROM technologies;
DELETE FROM change_email_tokens;
DELETE FROM password_reset_tokens;
DELETE FROM register_tokens;
DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (email, name, password, enabled)
VALUES ('user@gmail.com','John Doe', '{noop}password', true),
       ('admin@gmail.com','Jack', '{noop}admin', true),
       ('user2@gmail.com','Alice Key', '{noop}somePassword', true),
       ('userDisabled@gmail.com','Freeman25', '{noop}password', false);

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('USER', 100001),
       ('ADMIN', 100001),
       ('USER', 100002),
       ('USER', 100003);

INSERT INTO register_tokens (token, expiry_date, email, name, password)
VALUES ('5a99dd09-d23f-44bb-8d41-b6ff44275d01', '2024-08-06 19:35:56', 'some@gmail.com', 'someName', '{noop}somePassword'),
       ('52bde839-9779-4005-b81c-9131c9590d79', '2052-05-24 16:42:03', 'new@gmail.com', 'newName', '{noop}newPassword');

INSERT INTO password_reset_tokens (token, expiry_date, user_id)
VALUES ('5a99dd09-d23f-44bb-8d41-b6ff44275x97', '2052-02-05 12:10:00', 100000),
       ('52bde839-9779-4005-b81c-9131c9590b41', '2022-02-06 19:35:56', 100002),
       ('54ghh534-9778-4005-b81c-9131c9590c63', '2052-04-25 13:48:14', 100003);

INSERT INTO change_email_tokens(token, expiry_date, new_email, user_id)
VALUES ('5a49dd09-g23f-44bb-8d41-b6ff44275s56', '2024-08-05 21:49:01', 'some@gmail.com', 100001),
       ('1a43dx02-x23x-42xx-8r42-x6ff44275y67', '2052-01-22 06:17:32', 'someNew@gmail.com', 100002);

INSERT INTO technologies(name, href, img_file_name, img_file_link)
VALUES ('Java', 'https://www.oracle.com/java', 'java.svg', 'content/technologies/java/java.svg'),
       ('Spring', 'https://spring.io', 'spring.svg', 'content/technologies/spring/spring.svg'),
       ('Angular', 'https://angular.dev', 'angular.svg', 'content/technologies/angular/angular.svg');
