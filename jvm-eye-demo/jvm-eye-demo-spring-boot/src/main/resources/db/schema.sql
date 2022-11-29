-- schema.sql
create table users
(
    id int auto_increment,
    name     varchar(255) not null,
    password varchar(255) not null,
    age int not null,
    PRIMARY KEY (id)
);