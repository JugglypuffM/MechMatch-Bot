CREATE SEQUENCE IF NOT EXISTS public.matches_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE public.matches_id_seq
    OWNER TO postgres;

CREATE TABLE IF NOT EXISTS public.matches
(
    userid integer,
    friendid integer,
    id bigint default nextval('matches_id_seq'::regclass) not null
        primary key,
    isliked boolean,
    deleted boolean
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.matches
    OWNER to postgres;