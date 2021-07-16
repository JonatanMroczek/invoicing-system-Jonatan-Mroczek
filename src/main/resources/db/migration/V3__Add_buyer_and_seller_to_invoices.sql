ALTER TABLE PUBLIC.invoice
    ADD COLUMN buyer bigint NOT NULL;

ALTER TABLE PUBLIC.invoice
    ADD COLUMN seller bigint NOT NULL;

ALTER TABLE PUBLIC.invoice
    ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer)
        REFERENCES public.company (id);

ALTER TABLE PUBLIC.invoice
    ADD CONSTRAINT seller_fk FOREIGN KEY (seller)
        REFERENCES public.company (id);