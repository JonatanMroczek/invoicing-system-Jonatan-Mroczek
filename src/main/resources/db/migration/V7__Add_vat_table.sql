CREATE TABLE public.vat
( id bigserial NOT NULL,
name character varying(20),
rate numeric(3,2) NOT NULL,
PRIMARY KEY (id)
);

INSERT INTO public.vat (name, rate)
values ('23', 0.23),
       ('8', 0.08),
       ('5', 0.05),
       ('0', 0.00),
       ('ZW', 0.00);

ALTER TABLE public.vat
    OWNER to postgres;