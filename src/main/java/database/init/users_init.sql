CREATE TABLE IF NOT EXISTS public.users
(
    id text COLLATE pg_catalog."default" NOT NULL,
    name text COLLATE pg_catalog."default",
    age integer,
    sex text COLLATE pg_catalog."default",
    city text COLLATE pg_catalog."default",
    information text COLLATE pg_catalog."default",
    minexpectedage integer,
    maxexpectedage integer,
    expectedsex text COLLATE pg_catalog."default",
    expectedcity text COLLATE pg_catalog."default",
    photoid text COLLATE pg_catalog."default",
    localstate text COLLATE pg_catalog."default",
    globalstate text COLLATE pg_catalog."default",
    profilefilled boolean,
    username text COLLATE pg_catalog."default",
    suggestedfriendid text COLLATE pg_catalog."default",
    profileslist text COLLATE pg_catalog."default",
    profilespage integer,
    CONSTRAINT id PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;