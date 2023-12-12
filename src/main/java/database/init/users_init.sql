create table public.users
(
    id integer not null
        constraint users_pkey
            primary key,
    localstate text,
    globalstate text,
    suggestedfriendid text,
    profileslist text,
    profilespage integer,
    platform text,
    username text
);

alter table public.users owner to postgres;




