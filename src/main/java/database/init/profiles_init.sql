create table public.profiles
(
    id integer not null
        primary key,
    name text,
    age integer,
    sex text,
    city text,
    information text,
    minexpectedage integer,
    maxexpectedage integer,
    expectedsex text,
    expectedcity text,
    photoid text,
    profilefilled boolean
);

alter table public.profiles owner to postgres;