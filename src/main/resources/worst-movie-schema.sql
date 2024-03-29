drop table if exists movie_to_producer, movie_to_studio, studio, producer, movie, winner cascade;

create table studio
(
    id   bigint not null generated by default as identity,
    name character varying,
    primary key (id)
);

create table producer
(
    id           bigint not null generated by default as identity,
    fist_name    character varying,
    middle_names character varying,
    last_name    character varying,
    primary key (id)
);

create table movie
(
    id           bigint not null generated by default as identity,
    title        character varying,
    release_year integer,
    primary key (id)
);

create table movie_to_producer
(
    movie_id    bigint not null references movie (id),
    producer_id bigint not null references producer (id),
    primary key (movie_id, producer_id)
);


create table movie_to_studio
(
    movie_id  bigint not null references movie (id),
    studio_id bigint not null references studio (id),
    primary key (movie_id, studio_id)
);

create table winner
(
    prize_year integer not null,
    movie_id   bigint not null references movie (id)
);
