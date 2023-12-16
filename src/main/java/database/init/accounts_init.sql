create sequence public.internal_id_seq;

alter sequence public.internal_id_seq owner to postgres;

create table public.accounts
(
    id bigint default nextval('internal_id_seq'::regclass) not null
        constraint auth_pkey
            primary key,
    login text,
    passhash text,
    tgid text,
    dsid text,
    tgusername text,
    dsusername text,
    localstate text,
    globalstate text,
    suggestedfriendid text,
    pofileslist text,
    profilespage integer
);

alter table public.accounts owner to postgres;