create table public.clients
(
    platformid text not null
        primary key,
    globalstate text,
    localstate text,
    login text,
    platform text,
    loggedin boolean
);

alter table public.clients owner to postgres;




