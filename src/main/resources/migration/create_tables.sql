create table public.genres
(
    id   serial
        primary key,
    type varchar(100) not null
        unique
);

alter table public.genres
    owner to postgres;

create table public.authors
(
    id            bigserial
        primary key,
    full_name     varchar not null
        unique,
    year_of_birth integer not null
);

alter table public.authors
    owner to postgres;

create table public.books
(
    id        bigserial
        primary key,
    name      varchar not null,
    year      integer not null,
    author_id bigint  not null
        constraint books_authors_id_fk
            references public.authors
);

alter table public.books
    owner to postgres;

create table public.l_books_genres
(
    id       bigserial
        primary key,
    book_id  bigint  not null
        constraint l_books_genres_books_id_fk
            references public.books
            on update cascade on delete cascade,
    genre_id integer not null
        constraint l_books_genres_genres_id_fk
            references public.genres
            on update cascade on delete cascade
);

alter table public.l_books_genres
    owner to postgres;

