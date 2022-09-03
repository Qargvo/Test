CREATE TABLE IF NOT EXISTS users
(
    id integer generated always as identity,
    login   varchar(30) unique,
    password   varchar(40),
    primary key (id)
);
CREATE TABLE IF NOT EXISTS messages
(
    id integer primary key,
    loginId integer,
    message   varchar,
    foreign key (userId)
        references users(id)
);
CREATE TABLE IF NOT EXISTS tokens
(
    token varchar primary key,
    loginId integer,
    creationDate timestamp,
    foreign key (loginId)
        references users(id)
);