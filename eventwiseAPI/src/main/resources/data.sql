insert into role(name) values ('ROLE_ADMIN');
insert into role(name) values ('ROLE_USER');
insert into "user"(displayed_name, email, location, password, username) values ('TESTER','test@test.com','ISTANBUL', '$2a$10$o5H6Q1HY7W8KHQOtqDP7NOeR4rhn2bcq1Ys0XUeK3cfEVxOhFcRvy','string');
insert into user_roles(user_id, roles_id) values (1,2);