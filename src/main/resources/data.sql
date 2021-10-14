INSERT INTO ht_user(id, email, first_name, last_name, password)
VALUES (1, 'ta@example.com', 'Test', 'Admin', '$2a$10$AZVbPb6.rxOda1OowYuU.OOtdU5nRYiWNoqXMjcLtNiMAWWsklM56');
INSERT INTO ht_user(id, email, first_name, last_name, password)
VALUES (2, 'tu@example.com', 'Test', 'User', '$2a$10$ExnooQfv4b5HmPiwSxndDOimceevHhTx1r0fn8QXWG4pdvoXLMkB2');

INSERT INTO role(id, name)
VALUES (1, 'ADMIN');
INSERT INTO role(id, name)
VALUES (2, 'USER');

INSERT INTO ht_user_roles(ht_user_id, role_id)
VALUES (1, 1);
INSERT INTO ht_user_roles(ht_user_id, role_id)
VALUES (1, 2);
INSERT INTO ht_user_roles(ht_user_id, role_id)
VALUES (2, 2);