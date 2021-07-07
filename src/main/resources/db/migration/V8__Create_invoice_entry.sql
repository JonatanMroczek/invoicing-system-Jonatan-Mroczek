CREATE TABLE public.invoice_entry

(
    id bigserial NOT NULL,
    description character varying NOT NULL,
    quantity numeric (10, 2) NOT NULL DEFAULT 1,
    net_price numeric (10, 2) NOT NULL,
    vat_value numeric (10, 2) NOT NULL,
    vat_rate bigint NOT NULL,
    car_expenses bigint,
    PRIMARY KEY (id)
);

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT vat_rate_fk FOREIGN KEY (vat_rate)
        REFERENCES public.vat (id);

ALTER TABLE public.invoice_entry
    ADD CONSTRAINT car_fk FOREIGN KEY (car_expenses)
        REFERENCES public.car (id);

ALTER TABLE public.invoice_entry
    OWNER to postgres;