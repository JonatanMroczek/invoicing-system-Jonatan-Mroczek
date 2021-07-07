ALTER TABLE PUBLIC.invoices
    ADD COLUMN buyer bigint NOT NULL;

ALTER TABLE PUBLIC.invoices
    ADD COLUMN seller bigint NOT NULL;

ALTER TABLE PUBLIC.invoices
    ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer)
        REFERENCES public.companies (id);

ALTER TABLE PUBLIC.invoices
    ADD CONSTRAINT seller_fk FOREIGN KEY (seller)
        REFERENCES public.companies (id);