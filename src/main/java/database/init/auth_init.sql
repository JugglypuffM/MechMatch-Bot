create sequence public.internal_id_seq;

alter sequence public.internal_id_seq owner to postgres;

create table public.auth
(
    id bigint default nextval('internal_id_seq'::regclass) not null
        primary key,
    login text,
    passhash text,
    tgid text,
    dsid text
);

alter table public.auth owner to postgres;